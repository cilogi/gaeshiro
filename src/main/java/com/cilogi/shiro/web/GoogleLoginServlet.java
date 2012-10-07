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
import com.cilogi.shiro.gae.UserAuthType;
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
 * You are logged in to the suer service when you get here, and if there is no GAEUser with your Email
 * we create one (tagged as a Google user).
 * <p>When the <code>DatastoreRealm</code> is queried about this user (with dummy password) it authenticates
 * if the Google user has the same Email as the GAEUser.  This seems to be secure.
 * <p>One tiny fly in the ointment is that we really log you out on logout, which means that your browser
 * is logged out & you'll have to re-authenticate with other Google services.  I guess we could make this
 * an option if users find it too annoying.
 */
@Singleton
public class GoogleLoginServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(GoogleLoginServlet.class.getName());


    @Inject
    public GoogleLoginServlet(Provider<UserDAO> daoProvider) {
        super(daoProvider);
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
                    return;
                }

                String username = currentUser.getEmail();
                if (!createShiroUser(username)) {
                    issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "cannot authorize " + username
                             + " as you have registered the same Email before, as a non-Google email, which we don't allow", response);
                    return;
                }
                
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
            } else {
                // The idea is make sure that the user is logged in with the User Service
                // before logging in for Shiro
                ensureLoggedInToUserService(userService, request, response);
            }
        } catch (Exception e) {
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage(), response);
        }
    }

    private boolean createShiroUser(String userName) {
        UserDAO dao = daoProvider.get();
        GaeUser user = dao.findUser(userName);
        if (user == null) {
            user = new GaeUser(userName, "password", Sets.newHashSet("user"), Sets.<String>newHashSet());
            user.setUserAuthType(UserAuthType.GOOGLE);
            user.register();
            dao.saveUser(user, true);
            return true;
        }
        return user.getUserAuthType() == UserAuthType.GOOGLE;
    }

    private static void ensureLoggedInToUserService(UserService userService,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        logoutGoogleIfLoggedIn(request, response, userService);
        String authUrl = userService.createLoginURL("/user/admin/googleLoginAuth");
        response.sendRedirect(response.encodeRedirectURL(authUrl));
    }

    // make sure we're logged out before trying to log in.  Otherwise
    // the login can be <em>silent</em>. This is problematic when (a) you'd like
    // to log in as a different Google user, and (b) when we need to re-authorize when
    // accessing sensite resources where <code>isRemembered</code> isn't enough (e.g. kids accessing financial accounts).
    private static void logoutGoogleIfLoggedIn(HttpServletRequest request, HttpServletResponse response, UserService service) throws IOException{
        User user = service.getCurrentUser();
        if (user != null) {
            String redirectUrl = request.getRequestURL().toString();
            WebUtil.logoutGoogle(request, response, redirectUrl);
        }
    }
}
