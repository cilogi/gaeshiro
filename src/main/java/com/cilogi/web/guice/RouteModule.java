// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        RouteModule.java  (05-Oct-2010)
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


package com.cilogi.web.guice;

import com.cilogi.web.servlets.FreemarkerServlet;
import com.cilogi.web.servlets.WakeServlet;
import com.cilogi.web.servlets.appengine.GoogleLoginServlet;
import com.cilogi.web.servlets.oauth.OAuthLoginServlet;
import com.cilogi.web.servlets.persona.PersonaLoginServlet;
import com.cilogi.web.servlets.user.*;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.apphosting.utils.servlet.SessionCleanupServlet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import org.apache.shiro.web.servlet.ShiroFilter;

import java.util.Map;
import java.util.logging.Logger;


public class RouteModule extends ServletModule {
    static final Logger LOG = Logger.getLogger(RouteModule.class.getName());

    private final String userBaseUrl;
    
    public RouteModule(String userBaseUrl) {
        Preconditions.checkArgument(userBaseUrl != null && !userBaseUrl.endsWith("/"));
        this.userBaseUrl = userBaseUrl;
    }

    @Override
    protected void configureServlets() {
        filter("/*").through(ShiroFilter.class);
        filter("/*").through(AsyncCacheFilter.class);
        filter("/*").through(AppstatsFilter.class, map("calculateRpcCosts", "true"));

        serve("*.ftl").with(FreemarkerServlet.class);

        serve(userBaseUrl + "/socialLogin").with(OAuthLoginServlet.class);
        serve(userBaseUrl + "/googleLogin").with(GoogleLoginServlet.class);
        serve(userBaseUrl + "/personaLogin").with(PersonaLoginServlet.class);
        serve(userBaseUrl + "/status").with(StatusServlet.class);
        serve(userBaseUrl + "/list").with(UserListServlet.class);
        serve(userBaseUrl + "/suspend").with(UserSuspendServlet.class);

        serve("/login").with(LoginServlet.class);

        serve("/_ah/sessioncleanup").with(SessionCleanupServlet.class);
        serve("/appstats/*").with(AppstatsServlet.class);
        serve("/cron/wake").with(WakeServlet.class);
    }

    private static Map<String,String> map(String... params) {
        Preconditions.checkArgument(params.length % 2 == 0, "You have to have a n even number of map params");
        Map<String,String> map = Maps.newHashMap();
        for (int i = 0; i < params.length; i+=2) {
            map.put(params[i], params[i+1]);
        }
        return map;
    }
}
