// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        LoginServlet.java  (31-Oct-2011)
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

import com.cilogi.shiro.gaeuser.GaeUserDAO;
import com.cilogi.shiro.web.BaseServlet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;


@Singleton
public class LoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());

    @Inject
    LoginServlet(GaeUserDAO gaeUserDAO) {
        super(gaeUserDAO);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showView(response, "login.ftl");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String password = WebUtils.getCleanParam(request, PASSWORD);
            String username = WebUtils.getCleanParam(request, USERNAME);
            boolean rememberMe = WebUtils.isTrue(request, REMEMBER_ME);
            String host = request.getRemoteHost();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe, host);
            try {
                Subject subject = SecurityUtils.getSubject();
                loginWithNewSession(token, subject);
                //subject.login(token);
                issueJson(response, HTTP_STATUS_OK, MESSAGE, "ok");
            } catch (AuthenticationException e) {
                issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "cannot authorize " + username + ": " + e.getMessage(), response);
            }
        } catch (Exception e) {
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage(), response);
        }
    }
}
