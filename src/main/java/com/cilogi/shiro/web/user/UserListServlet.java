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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Map;
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
        Map<String,Object> map = Maps.newHashMap();
        map.put("iTotalRecords", nUsers);
        map.put("iTotalDisplayRecords", nUsers);
        map.put("sEcho", echo);

        List<GaeUser> users = users(dao, sSearch, start, length);
        map.put("aaData", users);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat("MMM dd yyyy"));
        issue(MIME_APPLICATION_JSON, HTTP_STATUS_OK, mapper.writeValueAsString(map), response);
    }

    private List<GaeUser> users(GaeUserDAO dao, String sSearch, int start, int length) {
        if (sSearch != null && !"".equals(sSearch)) {
            GaeUser user = dao.findUser(sSearch);
            List<GaeUser> list =  Lists.newArrayList();
            if (user != null) {
                list.add(user);
            }
            return list;
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
}
