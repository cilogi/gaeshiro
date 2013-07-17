// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        ConfirmServlet.java  (02-Nov-2011)
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
import com.google.common.collect.Sets;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

@Singleton
public class ConfirmServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(ConfirmServlet.class.getName());

    @Inject
    ConfirmServlet(Provider<GaeUserDAO> daoProvider) {
        super(daoProvider);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String code = request.getParameter(CODE);
            String userName = request.getParameter(USERNAME);
            String password = request.getParameter(PASSWORD);
            boolean isChange = "true".equals(request.getParameter(FORGOT));

            GaeUserDAO dao = new GaeUserDAO();
            GaeUser user = dao.findUserFromValidCode(code);
            if (user != null) {
                // can't do this in a transaction as we need to update the counter, and I don't
                // want to use the counter as the parent as it will not be efficient, and you
                // can only transact a parent/child group in GAE.  Should be OK if a single person
                // owns and transacts for a given user name.
                user.setPassword(password);
                dao.saveUser(user, false);

                dao.register(user);

                Subject sub = SecurityUtils.getSubject();
                UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
                sub.login(token);

                issueJson(response, HTTP_STATUS_OK,
                        MESSAGE, isChange
                                ? "OK, password changed for user name " + userName
                                : "OK, you're registered with user name " + userName);
            } else {
                issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND,
                      "Wrong code, or code is expired: \"" + code +"\", you'll need to retry", response);
            }
        } catch (Exception e) {
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                  "Oops, error in confirm: " + e.getMessage(), response);
        }
    }

    private Set<String> defaultRoles() {
        return Sets.newHashSet("user");
    }

    private Set<String> defaultPermissions() {
        return Sets.newHashSet();
    }

}
