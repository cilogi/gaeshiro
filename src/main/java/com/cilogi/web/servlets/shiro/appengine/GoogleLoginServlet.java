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


package com.cilogi.web.servlets.shiro.appengine;

import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.shiro.providers.googlegae.GoogleGAEAuthenticationToken;
import com.cilogi.shiro.providers.oauth.UserAuthType;
import com.cilogi.util.MimeTypes;
import com.cilogi.web.servlets.shiro.BaseServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Google authentication is handled using the built-in service API, rather than calls
 * to OAuth, as for Facebook.  This is deliberate as if you only want this, then you don't
 * need to include the OAuth library.
 * <p>The idea is that you login by (a) logging in the user service and then (b) logging in to
 * Shiro with an access token, whose password is a dummy.  You "check" the password by making sure
 * that the logged-in user is the same as the one you are authenticating.
 * <p>What happens is that a request to URL "googleLogin" is made.  This then logs you
 * in with GAE user service.
 * Once this is done, and you're definitely logged in there is a redirect to URL "googleLoginAuth".
 * You are logged in to the user service when you get here, and if there is no GAEUser with your Email
 * we create one (tagged as a Google user).
 * <p>When the <code>GaeUserRealm</code> is queried about this user (with dummy password) it authenticates
 * if the Google user has the same Email as the GAEUser.  This seems to be secure.
 * <p>One tiny fly in the ointment is that we really log you out on logout, which means that your browser
 * is logged out & you'll have to re-authenticate with other Google services.  I guess we could make this
 * an option if users find it too annoying.
 */
@Singleton
public class GoogleLoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(GoogleLoginServlet.class.getName());


    @Inject
    public GoogleLoginServlet(IGaeUserDAO gaeUserDAO) {
        super(gaeUserDAO);
    }


    @Override
    // this is called from login, and all that's required is to login via the Google URl with a return
    // to this place, but with GET as the verb
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService =  UserServiceFactory.getUserService();
        String currentUri = WebUtils.getRequestUri(request);
        String authUrl = userService.createLoginURL(currentUri);
        try {
            response.sendRedirect(response.encodeRedirectURL(authUrl));
        } catch (Exception e) {
            LOG.warning("Error trying to redirect to " + authUrl);
            response.sendRedirect("/");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService =  UserServiceFactory.getUserService();
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                issue(MimeTypes.MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "cannot login as we can't find Google user", response);
                return;
            }
            String userName = currentUser.getEmail();
            gaeUserDAO.ensureExists(userName);
            
            String host = request.getRemoteHost();
            GoogleGAEAuthenticationToken token = new GoogleGAEAuthenticationToken(userName,  host);
            try {
                Subject subject = SecurityUtils.getSubject();
                setProviderInCookieComment(UserAuthType.GOOGLE.name());
                loginWithNewSession(token, subject);

                // go back to where Shiro thought we should go or to home if that's not set
                SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
                String redirectUrl = (savedRequest == null) ? "/" : savedRequest.getRequestUrl();
                response.sendRedirect(response.encodeRedirectURL(redirectUrl));
            } catch (AuthenticationException e) {
                issue(MimeTypes.MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "cannot authorize " + userName + ": " + e.getMessage(), response);
            }
        } catch (Exception e) {
            issue(MimeTypes.MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage(), response);
        }
    }
}
