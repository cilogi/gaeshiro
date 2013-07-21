// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        SettingsServlet.java  (14-Nov-2011)
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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class SettingsServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(SettingsServlet.class.getName());

    @Inject
    SettingsServlet(GaeUserDAO gaeUserDAO) {
        super(gaeUserDAO);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            GaeUserDAO dao = getGaeUserDAO();
            String userName = request.getParameter(USERNAME);
            String password = request.getParameter(PASSWORD);

            Subject subject = SecurityUtils.getSubject();
            String subjectID = (String)subject.getPrincipal();
            GaeUser user = dao.findUser(subjectID);
            if (subject.isAuthenticated() && user != null) {
                if (userName.equals(subjectID)) {
                    if (password != null) {
                        user.setPassword(password);
                        dao.saveUser(user, false);
                        issueJson(response, HTTP_STATUS_OK, MESSAGE, "password changed successfully");
                    } else {
                        issue(MIME_TEXT_PLAIN, HTTP_STATUS_FORBIDDEN, "Your password is invalid: " + password, response);
                    }
                } else {
                    issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "You're not " + userName, response);
                }
            }  else {
                if (user == null) {
                    issue(MIME_TEXT_PLAIN, HTTP_STATUS_FORBIDDEN, "You're not a user I can set the password for", response);
                }  else {
                    issue(MIME_TEXT_PLAIN, HTTP_STATUS_FORBIDDEN, "You're not authenticated", response);
                }
            }
        } catch (Exception e) {
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                  "Oops, error in settings: " + e.getMessage(), response);
        }
    }
}
