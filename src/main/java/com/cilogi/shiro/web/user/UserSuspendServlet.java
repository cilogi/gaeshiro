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

import com.cilogi.shiro.gaeuser.impl.GaeUser;
import com.cilogi.shiro.gaeuser.impl.GaeUserDAO;
import com.cilogi.shiro.gaeuser.IGaeRegisteredUser;
import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.shiro.web.BaseServlet;
import lombok.extern.java.Log;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@Log
public class UserSuspendServlet extends BaseServlet {

    private static final long serialVersionUID = 6663353833251862992L;

    @Inject
    UserSuspendServlet(IGaeUserDAO gaeUserDAO) {
        super(gaeUserDAO);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String userName = request.getParameter(USERNAME);
            IGaeRegisteredUser user = (IGaeRegisteredUser)gaeUserDAO.findUser(userName);
            if (user != null) {
                boolean isSuspend = Boolean.parseBoolean(request.getParameter(SUSPEND));
                boolean isDelete = Boolean.parseBoolean(request.getParameter(DELETE));
                if (isDelete) {
                    if (isCurrentUserAdmin()) {
                        gaeUserDAO.deleteUser(user.getName());
                        issueJson(response, HTTP_STATUS_OK,
                                MESSAGE, "User " + userName + " is deleted");
                    } else {
                        issueJson(response, HTTP_STATUS_OK,
                                MESSAGE, "Only admins can delete users", CODE, "404");
                    }
                } else {
                    if (isCurrentUserAdmin()) {
                        user.setSuspended(isSuspend);
                        gaeUserDAO.saveUser(user, false);
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
                log.warning("Can't find user " + userName);
                issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND,
                      "Can't find user " + userName, response);
            }
        } catch (Exception e) {
            log.severe("Suspend failure: " + e.getMessage());
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                  "Error generating JSON: " + e.getMessage(), response);
        }
    }
}
