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

import com.cilogi.shiro.gae.GaeUser;
import com.cilogi.shiro.gae.GaeUserDAO;
import com.cilogi.shiro.web.BaseServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.objectify.cmd.Query;
import org.json.JSONException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Singleton
public class UserListServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(UserListServlet.class.getName());

    private static final int MAX_QUERY_OFFSET = 50;

    @Inject
    UserListServlet(Provider<GaeUserDAO> daoProvider) {
        super(daoProvider);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int draw = intParameter("draw", request, -1);
            int start = intParameter("start", request, 0);
            int length = intParameter("length", request, 10);
            String search = request.getParameter("search[value]");
            HttpSession session = request.getSession();
            doOutput(session, response, search, start, length, draw);
        } catch (Exception e) {
            LOG.severe("Error posting to list: " + e.getMessage());
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                    "Error generating JSON: " + e.getMessage(), response);
        }
    }

    private void doOutput(HttpSession session, HttpServletResponse response, String sSearch, int start, int length, int draw)
            throws JSONException, IOException {
        GaeUserDAO dao = new GaeUserDAO();
        long nUsers = dao.getCount();
        Map<String,Object> map = Maps.newHashMap();
        map.put("recordsTotal", nUsers);
        map.put("recordsFiltered", nUsers);
        map.put("draw", draw);

        List<GaeUser> users = users(session, dao, sSearch, start, length);
        map.put("data", users);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat("MMM dd yyyy"));
        String output = mapper.writeValueAsString(map);
        issue(MIME_APPLICATION_JSON, HTTP_STATUS_OK, output, response);
    }

    private List<GaeUser> users(HttpSession session, GaeUserDAO dao, String sSearch, int start, int length) {
        if (sSearch != null && !"".equals(sSearch)) {
            GaeUser user = dao.findUser(sSearch);
            List<GaeUser> list =  Lists.newArrayList();
            if (user != null) {
                list.add(user);
            }
            return list;
        } else {
            List<GaeUser> list = Lists.newArrayList();

            Cursor cursor = (Cursor)session.getAttribute("cursor_" + start);
            if (cursor == null && start >= MAX_QUERY_OFFSET) {
                // Doing a query with an offset is very expensive as you have to read through
                // everything up to the offset.  So we just bail out if that it the case. The font
                // end should display an error of some sort.
                LOG.warning("Can't process query for offset " + start + " as its too expensive");
                return list;
            }

            Query<GaeUser> query =  ofy().load().type(GaeUser.class)
                    .limit(length)
                    .order("-dateRegistered");

            query = (cursor != null) ? query.startAt(cursor) : query.offset(start);

            QueryResultIterator<GaeUser> it = query.iterator();
            while (it.hasNext()) {
                GaeUser user = it.next();
                list.add(user);
            }

            session.setAttribute("cursor_" + (start+length), it.getCursor());
            return list;
        }
    }
}
