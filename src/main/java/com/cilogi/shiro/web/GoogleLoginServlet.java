// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        GoogleLoginServlet.java  (04-Oct-2012)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
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
import com.cilogi.shiro.gae.UserDAO;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Sets;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class GoogleLoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(GoogleLoginServlet.class.getName());

    private Provider<UserDAO> daoProvider;

    @Inject
    public GoogleLoginServlet(Provider<UserDAO> daoProvider) {
        this.daoProvider = daoProvider;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService =  UserServiceFactory.getUserService();
        try {
            String currentUri = WebUtils.getRequestUri(request);
            if (currentUri.endsWith("googleLoginAuth")) {
                User currentUser = userService.getCurrentUser();
                if (currentUser == null) {
                    issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "cannot login for unknown reasons", response);
                }  else {
                    String username = currentUser.getEmail();
                    createShiroUser(username);
                    boolean rememberMe = true;
                    String host = request.getRemoteHost();
                    UsernamePasswordToken token = new UsernamePasswordToken(username, "password", rememberMe, host);
                    try {
                        Subject subject = SecurityUtils.getSubject();
                        loginWithNewSession(token, subject);
                        // go back to where Shiro thought we should go or to home if that's not set
                        SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
                        String redirectUrl = (savedRequest == null) ? "/index.html" : savedRequest.getRequestUrl();
                        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
                    } catch (AuthenticationException e) {
                        issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "cannot authorize " + username + ": " + e.getMessage(), response);
                    }
                }
            } else {
                createUserAsNeeded(userService, response);
            }
        } catch (Exception e) {
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage(), response);
        }
    }

    private void createShiroUser(String userName) {
        UserDAO dao = daoProvider.get();
        GaeUser user = dao.findUser(userName);
        if (user == null) {
            user = new GaeUser(userName, "password", Sets.newHashSet("user"), Sets.<String>newHashSet());
            user.setGoogle(true);
            user.register();
            dao.saveUser(user, true);
        }
    }

    private static void createUserAsNeeded(UserService userService,
                                           HttpServletResponse response) throws IOException {
        String authUrl = userService.createLoginURL("/user/admin/googleLoginAuth");
        response.sendRedirect(response.encodeRedirectURL(authUrl));
    }

}
