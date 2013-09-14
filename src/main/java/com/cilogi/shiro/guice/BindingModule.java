// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        BindingModule.java  (12-Oct-2011)
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


import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.shiro.gaeuser.impl.GaeUserDAO;
import com.cilogi.shiro.providers.oauth.provider.FacebookAuth;
import com.cilogi.shiro.providers.oauth.provider.IOAuthProviderInfo;
import com.cilogi.util.ICounter;
import com.cilogi.util.doc.CreateDoc;
import com.cilogi.util.gae.db.UserCounterDAO;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.apphosting.utils.servlet.SessionCleanupServlet;
import com.google.common.base.Charsets;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import org.apache.shiro.web.servlet.ShiroFilter;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Logger;


public class BindingModule extends AbstractModule {
    static final Logger LOG = Logger.getLogger(BindingModule.class.getName());


    private final String userBaseUrl;
    private final String staticBaseUrl;

    public BindingModule(String userBaseUrl, String staticBaseUrl) {
        this.userBaseUrl = userBaseUrl;
        this.staticBaseUrl = staticBaseUrl;
    }

    @Override
    protected void configure() {
        bind(ICounter.class).to(UserCounterDAO.class).in(Scopes.SINGLETON);
        bind(IGaeUserDAO.class).to(GaeUserDAO.class);
        bind(GaeUserDAO.class).in(Scopes.SINGLETON);
        bind(IOAuthProviderInfo.class).to(FacebookAuth.class);
        bind(CreateDoc.class).toInstance(createDoc());
        bind(ShiroFilter.class).in(Scopes.SINGLETON);
        bind(AppstatsServlet.class).in(Scopes.SINGLETON);
        bind(AppstatsFilter.class).in(Scopes.SINGLETON);
        bind(AsyncCacheFilter.class).in(Scopes.SINGLETON);// needed to sync the datastore if its running async
        bind(SessionCleanupServlet.class).in(Scopes.SINGLETON); // needed to cleanup stale sessions from datastore

        bindString("tim", "tim");
        bindString("email.from", "admin@gaeshiro.appspotmail.com");
        bindString("userBaseUrl", userBaseUrl);
        bindString("staticBaseUrl", staticBaseUrl);
        bindString("social.site", isDevelopmentServer() ? "local" : "live");
        bindString("host", isDevelopmentServer() ? "http://localhost:8080" : "https://personashiro.appspot.com:443");
    }

    private void bindString(String key, String value) {
        bind(String.class).annotatedWith(Names.named(key)).toInstance(value);
    }

    private CreateDoc createDoc() {
        try {
            URL base = getClass().getResource("/ftl/");
            CreateDoc create = new CreateDoc(base, Locale.getDefault(), Charsets.UTF_8.name());
            Configuration cfg = create.cfg();
            cfg.setSharedVariable("userBaseUrl", userBaseUrl);
            cfg.setSharedVariable("staticBaseUrl", staticBaseUrl);
            return create;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isDevelopmentServer() {
        SystemProperty.Environment.Value server = SystemProperty.environment.value();
        return server == SystemProperty.Environment.Value.Development;
    }
}
