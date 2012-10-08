// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        ServeModule.java  (05-Oct-2010)
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


package com.cilogi.shiro.guice;

import com.cilogi.shiro.web.*;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import org.apache.shiro.web.servlet.ShiroFilter;

import java.util.Map;
import java.util.logging.Logger;


public class ServeModule extends ServletModule {
    static final Logger LOG = Logger.getLogger(ServeModule.class.getName());

    private final String userBaseUrl;
    
    public ServeModule(String userBaseUrl) {
        Preconditions.checkArgument(userBaseUrl != null && !userBaseUrl.endsWith("/"));
        this.userBaseUrl = userBaseUrl;
    }

    @Override
    protected void configureServlets() {
        filter("/*").through(ShiroFilter.class);
        filter("/*").through(AsyncCacheFilter.class);
        filter("/*").through(AppstatsFilter.class, map("calculateRpcCosts", "true"));
        /*
        serve("*.ftl").with(ShiroFreemarkerServlet.class, map(
                "TemplatePath", "/WEB-INF/classes/ftl",
                "NoCache", "true",
                "ContentType", "text/html;charset=UTF-8",
                "template_update_delay", "0",
                "default_encoding", "UTF-8",
                "number_format", "0.###"
        ));
        */
        serve("*.ftl").with(FreemarkerServlet.class);
        serve(userBaseUrl + "/ajaxLogin").with(LoginServlet.class);

        serve(userBaseUrl + "/oauthLogin").with(OAuthLoginServlet.class);
        serve("/oauth2callback").with(OAuthLoginServlet.class);

        serve(userBaseUrl + "/googleLogin").with(GoogleLoginServlet.class);
        serve(userBaseUrl + "/googleLoginAuth").with(GoogleLoginServlet.class);
        serve(userBaseUrl + "/register").with(RegisterServlet.class);
        serve(userBaseUrl + "/registermail").with(MailQueueServlet.class);
        serve(userBaseUrl + "/confirm").with(ConfirmServlet.class);
        serve(userBaseUrl + "/status").with(StatusServlet.class);
        serve(userBaseUrl + "/list").with(UserListServlet.class);
        serve(userBaseUrl + "/suspend").with(UserSuspendServlet.class);
        serve(userBaseUrl + "/settings").with(SettingsServlet.class);
            // this one is here so that the default login filter works
        serve("/login.jsp").with(LoginJSPServlet.class);
            // Lets check mail to see when stuff bounces
        serve("/_ah/mail/*").with(MailReceiveServlet.class);
        serve("/appstats/*").with(AppstatsServlet.class);
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
