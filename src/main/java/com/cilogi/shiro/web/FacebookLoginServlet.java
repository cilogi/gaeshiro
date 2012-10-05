// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        FacebookLoginServlet.java  (05-Oct-2012)
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
import com.cilogi.shiro.gae.UserAuthType;
import com.cilogi.shiro.gae.UserDAO;
import com.cilogi.shiro.service.FacebookAuth;
import com.google.common.collect.Sets;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class FacebookLoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(FacebookLoginServlet.class.getName());

    private final FacebookAuth auth;
    private final Provider<UserDAO> daoProvider;

    @Inject
    public FacebookLoginServlet(Provider<UserDAO> daoProvider) {
        this.daoProvider = daoProvider;
        this.auth = new FacebookAuth();
    }

    /**
     * Step 1 is to send off a request.
     * @param request request
     * @param response response
     * @throws ServletException if something goes wrong
     * @throws IOException if we can't write stuff
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String currentUri = WebUtils.getRequestUri(request);
            String url = auth.loginURL(currentUri);
            WebUtils.issueRedirect(request, response, url);
        } catch (Exception e) {
            issue("text/plain", 500, "Something unexpected went wrong: " + e.getMessage(), response);
        }
    }

    // Step 2 is the return from Facebook, either giving permission and returning the email, or not...
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = WebUtils.getCleanParam(request, "code");
        JSONObject info = auth.getUserInfo(code);
        try {
            if (info.has("error")) {
                String message = info.getJSONObject("error").getString("message");
                issue("text/plain", 400, "Couldn't get Facebook permission: " + message, response);
            } else {
                String email = info.getString("email");
                UserDAO dao = daoProvider.get();
                GaeUser user = dao.findUser(email);
                if (user == null) {
                    user = new GaeUser(email, "password", Sets.newHashSet("user"), Sets.<String>newHashSet());
                    user.setUserAuthType(UserAuthType.FACEBOOK);
                    dao.saveUser(user, true);
                }
                if (user.getUserAuthType() != UserAuthType.FACEBOOK) {
                    issue("text/plain", 400, "You can't log in with Facebook if you're already registered via " + user.getUserAuthType(), response);
                    return;
                }
                recordPassForUser(email);

                boolean rememberMe = true;
                String host = request.getRemoteHost();
                UsernamePasswordToken token = new UsernamePasswordToken(email, "password", rememberMe, host);

                Subject subject = SecurityUtils.getSubject();
                loginWithNewSession(token, subject);

                SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
                String redirectUrl = (savedRequest == null) ? "/index.html" : savedRequest.getRequestUrl();
                response.sendRedirect(response.encodeRedirectURL(redirectUrl));
            }
            // redirect to wherever you were going, or to home
        } catch (Exception e) {
            issue("text/plain", 500, "Something unexpected went wrong: " + e.getMessage(), response);
        }
    }

    private void recordPassForUser(String email) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        session.setAttribute("cilogi_logged_in" + email, "true");
    }


}
