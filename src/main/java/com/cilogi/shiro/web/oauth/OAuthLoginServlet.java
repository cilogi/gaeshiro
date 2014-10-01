// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        OAuthLoginServlet.java  (05-Oct-2012)
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


package com.cilogi.shiro.web.oauth;

import com.cilogi.shiro.gae.GaeUser;
import com.cilogi.shiro.gae.UserAuthType;
import com.cilogi.shiro.gae.GaeUserDAO;
import com.cilogi.shiro.oauth.OAuthAuthenticationToken;
import com.cilogi.shiro.oauth.OAuthInfo;
import com.cilogi.shiro.oauth.provider.FacebookAuth;
import com.cilogi.shiro.oauth.provider.GoogleAuth;
import com.cilogi.shiro.oauth.provider.IOAuthProviderInfo;
import com.cilogi.shiro.web.BaseServlet;
import com.google.common.collect.Sets;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

// This is set up so that its possible to user other types of OAuth provider rather easily
@Singleton
public class OAuthLoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(OAuthLoginServlet.class.getName());

    private final String site;

    @Inject
    public OAuthLoginServlet(@Named("social.site") String site, Provider<GaeUserDAO> daoProvider) {
        super(daoProvider);
        this.site = site;
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
            IOAuthProviderInfo auth = getProvider(request);
            putSessionData("userAuthType", auth.getUserAuthType().name());

            String url = isReAuthenticate() ? auth.reAuthenticateURL(currentUri) : auth.loginURL(currentUri);
            WebUtils.issueRedirect(request, response, url);
        } catch (Exception e) {
            issue("text/plain", 500, "Something unexpected went wrong when posting: " + e.getMessage(), response);
        }
    }

    // Step 2 is the return from Facebook, either giving permission and returning the email, or not...
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = WebUtils.getCleanParam(request, "code");
        String currentUri = WebUtils.getRequestUri(request);
        try {
            IOAuthProviderInfo auth = getProvider((String)getAndClearSessionData("userAuthType"));
            OAuthInfo info = auth.getUserInfo(code, currentUri);
            if (info.isError()) {
                String message = info.getErrorString();
                issue("text/plain", 400, "Couldn't get " + info.getUserAuthType() + " permission: " + message, response);
            } else {
                String email = info.getEmail();
                GaeUserDAO dao = daoProvider.get();
                GaeUser user = dao.findUser(email);
                if (user == null) {
                    user = new GaeUser(email, Sets.newHashSet("user"), Sets.<String>newHashSet());
                    user.register();
                    dao.saveUser(user, true);
                }

                OAuthAuthenticationToken token = new OAuthAuthenticationToken(info.getToken(), info.getUserAuthType(), email, request.getRemoteHost());

                Subject subject = SecurityUtils.getSubject();
                subject.login(token);

                // redirect to wherever you were going, or to home
                SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
                String redirectUrl = (savedRequest == null) ? "/index.html" : savedRequest.getRequestUrl();
                // revoking the token immediately is (a) safe as the token can't float around anywhere, and (b) lets us re-suthenticate and logout properly.
                // Just seems odd as the token is meant to last a long time...
                auth.revokeToken(info.getToken(), request, response, redirectUrl);
                //response.sendRedirect(response.encodeRedirectURL(redirectUrl));
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
            return user != null;
        } else {
            return false;
        }
    }

    private IOAuthProviderInfo getProvider(String authType) {
        return getProvider(authType, site);
    }

    private IOAuthProviderInfo getProvider(HttpServletRequest request) {
        String authType = request.getParameter("provider");
        return getProvider(authType, site);
    }

    private static void putSessionData(String key, Object data) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        session.setAttribute(key, data);
    }

    private static Object getAndClearSessionData(String key) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Object data = session.getAttribute(key);
        session.removeAttribute(key);
        return data;
    }


    private static IOAuthProviderInfo getProvider(String name, String site) {
        try {
            UserAuthType type = UserAuthType.valueOf(name);
            switch (type) {
                case GOOGLE:
                    return new GoogleAuth(site);
                case FACEBOOK:
                    return new FacebookAuth(site);
                default:
                    LOG.warning("Auth type " + name + "isn't handled");
                    return null;
            }
        } catch (Exception e) {
            LOG.warning("Can't work out the auth type of " + name);
            return null;
        }
    }


}
