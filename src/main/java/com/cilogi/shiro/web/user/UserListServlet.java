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


package com.cilogi.shiro.web.user;

import com.cilogi.shiro.gaeuser.GaeUser;
import com.cilogi.shiro.gaeuser.GaeUserDAO;
import com.cilogi.shiro.web.BaseServlet;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Singleton
public class UserListServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(UserListServlet.class.getName());

    @Inject
    UserListServlet(Provider<GaeUserDAO> daoProvider) {
        super(daoProvider);
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

    private void doOutput(HttpServletResponse response, String sSearch, int start, int length, String echo)
            throws JSONException, IOException {
        GaeUserDAO dao = new GaeUserDAO();
        long nUsers = dao.getCount();
        JSONObject obj = new JSONObject();
        obj.put("iTotalRecords", nUsers);
        obj.put("iTotalDisplayRecords", nUsers);
        obj.put("sEcho", echo);

        List<GaeUser> users = users(dao, sSearch, start, length);
        JSONArray array = new JSONArray();
        int index = 0;
        for (GaeUser user : users) {
            JSONArray arr = new JSONArray();
            arr.put(user.getName());
            arr.put(dateFrom(user.getDateRegistered()));
            arr.put(set2string(user.getRoles()));
            arr.put(String.format("<input data-start=\"%d\" data-length=\"%d\" type=\"checkbox\" name=\"%s\" %s>",
                    start, length,
                    user.getName(), user.isSuspended() ? "checked" : ""));
            arr.put(String.format("<input data-start=\"%d\" data-length=\"%d\" data-index=\"%d\" type=\"button\" name=\"%s\" value=\"delete\">",
                    start, length, index,
                    user.getName()));
            array.put(arr);
            index++;
        }
        obj.put("aaData", array);
        issueJson(response, HTTP_STATUS_OK, obj);
    }

    private List<GaeUser> users(GaeUserDAO dao, String sSearch, int start, int length) {
        if (sSearch != null && !"".equals(sSearch)) {
            return Lists.newArrayList(dao.findUser(sSearch));
        } else {
            List<GaeUser> list =  ofy().load().type(GaeUser.class)
                    .offset(start)
                    .limit(length)
                    .order("-dateRegistered")
                    .list();
            LOG.info("Fresh load start " + start + " # " + length);
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
}
