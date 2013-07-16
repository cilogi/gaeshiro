// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        UserSuspendServlet.java  (12-Nov-2011)
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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class UserSuspendServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(UserSuspendServlet.class.getName());

    @Inject
    UserSuspendServlet(Provider<GaeUserDAO> daoProvider) {
        super(daoProvider);    
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String userName = request.getParameter(USERNAME);
            GaeUserDAO dao = daoProvider.get();
            GaeUser user = dao.findUser(userName);
            if (user != null) {
                boolean isSuspend = Boolean.parseBoolean(request.getParameter(SUSPEND));
                boolean isDelete = Boolean.parseBoolean(request.getParameter(DELETE));
                if (isDelete) {
                    if (isCurrentUserAdmin()) {
                        dao.deleteUser(user);
                        issueJson(response, HTTP_STATUS_OK,
                                MESSAGE, "User " + userName + " is deleted");
                    } else {
                        issueJson(response, HTTP_STATUS_OK,
                                MESSAGE, "Only admins can delete users", CODE, "404");
                    }
                } else {
                    if (isCurrentUserAdmin()) {
                        user.setSuspended(isSuspend);
                        dao.saveUser(user, false);
                        issueJson(response, HTTP_STATUS_OK,
                                MESSAGE, isSuspend
                                        ? "User " + userName + " is suspended"
                                        : "User " + userName + " is not suspended");
                    } else {
                        issueJson(response, HTTP_STATUS_OK,
                                MESSAGE, "Only admins can suspend users", CODE, "404");

                    }
                }
            } else {
                LOG.warning("Can't find user " + userName);
                issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND,
                      "Can't find user " + userName, response);
            }
        } catch (Exception e) {
            LOG.severe("Suspend failure: " + e.getMessage());
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                  "Error generating JSON: " + e.getMessage(), response);
        }
    }
}
