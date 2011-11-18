// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        UserListServlet.java  (11-Nov-2011)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.shiro.web;

import com.cilogi.shiro.gae.GaeUser;
import com.cilogi.shiro.gae.MemcacheManager;
import com.cilogi.shiro.gae.UserDAO;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.shiro.cache.Cache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class UserListServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(UserListServlet.class.getName());

    private Cache<ListKey, List<GaeUser>> cache;

    UserListServlet() {
        cache = new MemcacheManager().getCache("UserList");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int iDisplayStart = intParameter(DATATABLE_START, request, -1);
            int iDisplayLength = intParameter(DATATABLE_LENGTH, request, -1);
            String sSearch = request.getParameter(DATATABLE_SEARCH);
            String sEcho = request.getParameter(DATATABLE_ECHO);
            doOutput(response, sSearch, iDisplayStart, iDisplayLength, sEcho);
        } catch (Exception e) {
            LOG.severe("Error posting to list: " + e.getMessage());
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                    "Error generating JSON: " + e.getMessage(), response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String invalidateCache = request.getParameter(INVALIDATE_CACHE);
            int start = intParameter("start", request, 0);
            int length = intParameter("length", request, 0);
            if (invalidateCache != null) {
                cache.remove(new ListKey(start, length));
            }
        } catch (Exception e) {
            LOG.severe("Error putting to list (invalidate cache): " + e.getMessage());
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                    "Error invalidating cache: " + e.getMessage(), response);
        }
    }

    private void doOutput(HttpServletResponse response, String sSearch, int start, int length, String echo)
            throws JSONException, IOException {
        gen();
        UserDAO dao = new UserDAO();
        long nUsers = dao.getCount();
        JSONObject obj = new JSONObject();
        obj.put("iTotalRecords", nUsers);
        obj.put("iTotalDisplayRecords", nUsers);
        obj.put("sEcho", echo);

        List<GaeUser> users = users(dao, sSearch, start, length);
        JSONArray array = new JSONArray();
        for (GaeUser user : users) {
            JSONArray arr = new JSONArray();
            arr.put(user.getName());
            arr.put(dateFrom(user.getDateRegistered()));
            arr.put(set2string(user.getRoles()));
            arr.put(String.format("<input data-start=\"%d\" data-length=\"%d\" type=\"checkbox\" name=\"%s\" value=\"%s\" %s>",
                    start, length,
                    user.getName(), user.getName(), user.isSuspended() ? "checked" : ""));
            array.put(arr);
        }
        obj.put("aaData", array);
        issueJson(response, HTTP_STATUS_OK, obj);
    }

    private List<GaeUser> users(UserDAO dao, String sSearch, int start, int length) {
        if (sSearch != null && !"".equals(sSearch)) {
            return Lists.newArrayList(dao.findUser(sSearch));
        } else {
            ListKey key = new ListKey(start, length);
            List<GaeUser> list =  cache.get(key);
            if (list == null) {
                list = Lists.newLinkedList();
                QueryResultIterable<GaeUser> query = dao.ofy().query(GaeUser.class)
                        .order("-dateRegistered")
                        .offset(start)
                        .limit(length)
                        .fetch();
                for (GaeUser aQuery : query) {
                    list.add(aQuery);
                }
                LOG.info("Fresh load start " + start + " # " + length);
            }
            cache.put(key, list);
            return list;
        }
    }

    private String dateFrom(Date date) {
        return (date == null)
                ? "<em>unregistered</em>"
                : new SimpleDateFormat("MMM dd yyyy").format(date);
    }

    private String set2string(Set<String> set) {
        boolean had = false;
        String out = "";
        for (String s : set) {
            out += had ? "," + s : s;
            had = true;
        }
        return out;
    }

    private void gen() {
        UserDAO dao = new UserDAO();
        GaeUser user = dao.findUser("user0@acme.com");
        if (user == null) {
            for (int i = 0; i < 100; i++) {
                user = new GaeUser("user" + i + "@acme.com", "acme", Sets.newHashSet("user"), Sets.<String>newHashSet());
                dao.saveUser(user, true);
            }
        }
    }

    private static class ListKey implements Serializable {
        private final int start;
        private final int length;

        ListKey(int start, int length) {
            this.start = start;
            this.length = length;
        }

        @Override
        public int hashCode() {
            return start * 503 + length;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ListKey) {
                ListKey k = (ListKey) o;
                return start == k.start && length == k.length;
            } else {
                return false;
            }
        }
    }
}
