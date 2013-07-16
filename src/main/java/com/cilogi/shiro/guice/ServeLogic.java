// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        ServeLogic.java  (12-Oct-2011)
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

import com.cilogi.shiro.gaeuser.GaeUserDAO;
import com.cilogi.shiro.oauth.provider.FacebookAuth;
import com.cilogi.shiro.oauth.provider.IOAuthProviderInfo;
import com.cilogi.util.doc.CreateDoc;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.common.base.Charsets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
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


public class ServeLogic extends AbstractModule {
    static final Logger LOG = Logger.getLogger(ServeLogic.class.getName());


    private final String userBaseUrl;
    private final String staticBaseUrl;

    public ServeLogic(String userBaseUrl, String staticBaseUrl) {
        this.userBaseUrl = userBaseUrl;
        this.staticBaseUrl = staticBaseUrl;
    }

    @Override
    protected void configure() {
        bind(IOAuthProviderInfo.class).to(FacebookAuth.class);
        bind(CreateDoc.class).toInstance(createDoc());
        bind(ShiroFilter.class).in(Scopes.SINGLETON);
        bind(AppstatsServlet.class).in(Scopes.SINGLETON);
        bind(AppstatsFilter.class).in(Scopes.SINGLETON);
        bind(AsyncCacheFilter.class).in(Scopes.SINGLETON);// needed to sync the datastore if its running async
        bindString("tim", "tim");
        bindString("email.from", "admin@gaeshiro.appspotmail.com");
        bindString("userBaseUrl", userBaseUrl);
        bindString("staticBaseUrl", staticBaseUrl);
        bindString("social.site", isDevelopmentServer() ? "local" : "live");
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

    @Provides
    GaeUserDAO provideUserDAO() {
        return new GaeUserDAO();
    }
}
