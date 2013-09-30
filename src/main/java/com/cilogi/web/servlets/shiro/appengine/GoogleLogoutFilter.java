// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        GoogleLogoutFilter.java  (04-Oct-2012)
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

package com.cilogi.web.servlets.shiro.appengine;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Log a user out of Google is we're using the built-in Google login.
 * This isn't needed for OAuth as we invalidate the token right away, and
 * its not needed for Persona as there is no server-side login trace.
 */
public class GoogleLogoutFilter extends LogoutFilter {
    static final Logger LOG = Logger.getLogger(GoogleLogoutFilter.class.getName());

    @Inject
    public GoogleLogoutFilter() {}

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.logout();
        } catch (SessionException ise) {
            LOG.info("Encountered session exception during logout.  This can generally safely be ignored: " + ise.getMessage());
        }
        logoutGoogleService(request, response, "/");
        return false;
    }

    private static void logoutGoogleService(ServletRequest request, ServletResponse response, String redirectUrl) throws IOException {
        UserService service = UserServiceFactory.getUserService();
        User user = service.getCurrentUser();
        if (user != null) {
            String logoutUrl = service.createLogoutURL(redirectUrl);
            WebUtils.issueRedirect(request, response, logoutUrl);
        } else {
            WebUtils.issueRedirect(request, response, redirectUrl);
        }
    }


}
