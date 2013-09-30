// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        ShiroRouteModule.java  (30/09/13)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Cilogi (the Author) and may not be used, sold, licenced, 
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


package com.cilogi.web.guice;

import com.cilogi.web.servlets.WakeServlet;
import com.cilogi.web.servlets.shiro.FreemarkerServlet;
import com.cilogi.web.servlets.shiro.appengine.GoogleLoginServlet;
import com.cilogi.web.servlets.shiro.oauth.OAuthLoginServlet;
import com.cilogi.web.servlets.shiro.persona.PersonaLoginServlet;
import com.cilogi.web.servlets.shiro.user.LoginServlet;
import com.cilogi.web.servlets.shiro.user.StatusServlet;
import com.cilogi.web.servlets.shiro.user.UserListServlet;
import com.cilogi.web.servlets.shiro.user.UserSuspendServlet;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.apphosting.utils.servlet.SessionCleanupServlet;
import com.google.common.base.Preconditions;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShiroRouteModule extends ServletModule {
    static final Logger LOG = LoggerFactory.getLogger(ShiroRouteModule.class);

    private final String userBaseUrl;

    public ShiroRouteModule(String userBaseUrl) {
        Preconditions.checkArgument(userBaseUrl != null && !userBaseUrl.endsWith("/"));
        this.userBaseUrl = userBaseUrl;
    }

    @Override
    protected void configureServlets() {
        filter("/*").through(ShiroFilter.class);
        serve("*.ftl").with(FreemarkerServlet.class);

        serve(userBaseUrl + "/socialLogin").with(OAuthLoginServlet.class);
        serve(userBaseUrl + "/googleLogin").with(GoogleLoginServlet.class);
        serve(userBaseUrl + "/personaLogin").with(PersonaLoginServlet.class);
        serve(userBaseUrl + "/status").with(StatusServlet.class);
        serve(userBaseUrl + "/list").with(UserListServlet.class);
        serve(userBaseUrl + "/suspend").with(UserSuspendServlet.class);

        serve("/login").with(LoginServlet.class);
    }

}
