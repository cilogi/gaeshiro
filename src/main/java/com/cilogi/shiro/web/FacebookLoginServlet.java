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
import com.cilogi.shiro.gae.oauth.OAuthAuthenticationToken;
import com.cilogi.shiro.gae.oauth.OAuthInfo;
import com.cilogi.shiro.service.FacebookAuth;
import com.cilogi.shiro.service.IOAuthProviderInfo;
import com.google.common.collect.Sets;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

// This is set up so that its possible to user other types of OAuth provider rather easily
@Singleton
public class FacebookLoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(FacebookLoginServlet.class.getName());

    private final IOAuthProviderInfo auth;

    @Inject
    public FacebookLoginServlet(Provider<UserDAO> daoProvider, IOAuthProviderInfo auth) {
        super(daoProvider);
        this.auth = auth;
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
            String url = isReAuthenticate() ? auth.reAuthenticateURL(currentUri) : auth.loginURL(currentUri);
            WebUtils.issueRedirect(request, response, url);
        } catch (Exception e) {
            issue("text/plain", 500, "Something unexpected went wrong: " + e.getMessage(), response);
        }
    }

    // Step 2 is the return from Facebook, either giving permission and returning the email, or not...
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = WebUtils.getCleanParam(request, "code");
        String currentUri = WebUtils.getRequestUri(request);
        try {
            OAuthInfo info = auth.getUserInfo(code, currentUri);
            if (info.isError()) {
                String message = info.getErrorString();
                issue("text/plain", 400, "Couldn't get " + info.getUserAuthType() + " permission: " + message, response);
            } else {
                String email = info.getEmail();
                UserDAO dao = daoProvider.get();
                GaeUser user = dao.findUser(email);
                if (user == null) {
                    user = new GaeUser(email, info.getUserAuthType(), Sets.newHashSet("user"), Sets.<String>newHashSet());
                    user.setAccessToken(info.getToken());
                    dao.saveUser(user, true);
                } else {
                    if (user.getUserAuthType() != auth.getUserAuthType()) {
                        issue("text/plain", 400, "You can't log in with Facebook if you're already registered via " + user.getUserAuthType(), response);
                        return;
                    }
                    user.setAccessToken(info.getToken());
                    dao.saveUser(user, false);
                }

                OAuthAuthenticationToken token = new OAuthAuthenticationToken(info.getToken(), info.getUserAuthType(), email, request.getRemoteHost());

                Subject subject = SecurityUtils.getSubject();
                subject.login(token);

                // redirect to wherever you were going, or to home
                SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
                String redirectUrl = (savedRequest == null) ? "/index.html" : savedRequest.getRequestUrl();
                response.sendRedirect(response.encodeRedirectURL(redirectUrl));
            }
        } catch (Exception e) {
            issue("text/plain", 500, "Something unexpected went wrong: " + e.getMessage(), response);
        }
    }

    private boolean isReAuthenticate() {
        Subject subject = SecurityUtils.getSubject();
        String principal = (String)subject.getPrincipal();
        if (principal != null) {
            GaeUser user = daoProvider.get().findUser(principal);
            return (user != null) && user.getAccessToken() != null;
        } else {
            return false;
        }
    }


}
