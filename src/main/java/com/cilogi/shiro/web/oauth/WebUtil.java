// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        WebUtil.java  (07-Oct-2012)
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
import com.cilogi.shiro.gae.UserDAOProvider;
import com.cilogi.shiro.oauth.provider.FacebookAuth;
import com.cilogi.shiro.oauth.provider.GoogleAuth;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Charsets;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONObject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


public class WebUtil {
    static final Logger LOG = Logger.getLogger(WebUtil.class.getName());

    private WebUtil() {}


    static void logoutGoogleService(ServletRequest request, ServletResponse response, String redirectUrl) throws IOException {
        UserService service = UserServiceFactory.getUserService();
        User user = service.getCurrentUser();
        if (user != null) {
            String logoutUrl = service.createLogoutURL(redirectUrl);
            WebUtils.issueRedirect(request, response, logoutUrl);
        } else {
            WebUtils.issueRedirect(request, response, redirectUrl);
        }
    }

    static void logoutGoogle(String token, ServletRequest request, ServletResponse response) throws IOException {
        String redirectHome = makeRoot(((HttpServletRequest)request).getRequestURL().toString());
        String url = GoogleAuth.logoutUrl(token);
        String reply = fetch(new URL(url));
        if (reply != null) {
            LOG.info("Failed to logout from Google: " + reply);
        }
        WebUtils.issueRedirect(request, response, redirectHome);
    }

    static  void logoutFacebook(String token, ServletRequest request, ServletResponse response) throws IOException {
        String redirectHome = makeRoot(((HttpServletRequest)request).getRequestURL().toString());

        String url = FacebookAuth.logoutUrl(redirectHome, token);

        HttpServletResponse httpResponse = (HttpServletResponse)response;
        httpResponse.sendRedirect(httpResponse.encodeRedirectURL(url));
    }

    public static String makeRoot(String fullURL) {
        try {
            URL url = new URL(fullURL);
            String portString = (url.getPort() == -1) ? "" : ":" + url.getPort();
            return url.getProtocol() + "://" + url.getHost() + portString + "/";
        } catch (MalformedURLException e) {
            return fullURL;
        }
    }

    static String fetch(URL url) {
        URLFetchService service = URLFetchServiceFactory.getURLFetchService();
        try {
            HTTPRequest request = new HTTPRequest(url, HTTPMethod.POST);
            HTTPResponse response = service.fetch(request);
            if (response.getResponseCode() == 200) {
                return null;
            } else {
                String s = new String(response.getContent(), Charsets.UTF_8);
                return (s == null) ? "Unknown error with code " + response.getResponseCode() : s;
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }


}
