// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        ShiroBindingModule.java  (30/09/13)
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

import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.shiro.gaeuser.impl.GaeUserDAO;
import com.cilogi.shiro.providers.oauth.provider.FacebookAuth;
import com.cilogi.shiro.providers.oauth.provider.IOAuthProviderInfo;
import com.cilogi.shiro.util.ICounter;
import com.cilogi.shiro.util.gaedb.UserCounterDAO;
import com.cilogi.util.doc.CreateDoc;
import com.cilogi.util.doc.ICreateDoc;
import com.cilogi.web.servlets.shiro.view.IRenderView;
import com.cilogi.web.servlets.shiro.view.RenderView;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.apphosting.utils.servlet.SessionCleanupServlet;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;


public class ShiroBindingModule extends AbstractModule {
    static final Logger LOG = LoggerFactory.getLogger(ShiroBindingModule.class);

    private final String userBaseUrl;
    private final String staticBaseUrl;

    public ShiroBindingModule(String userBaseUrl, String staticBaseUrl) {
        Preconditions.checkNotNull(userBaseUrl);
        Preconditions.checkNotNull(staticBaseUrl);

        this.userBaseUrl = userBaseUrl;
        this.staticBaseUrl = staticBaseUrl;
    }

    @Override
    protected void configure() {
        bind(IRenderView.class).to(RenderView.class).in(Scopes.SINGLETON);
        bind(ICounter.class).to(UserCounterDAO.class).in(Scopes.SINGLETON);
        bind(IGaeUserDAO.class).to(GaeUserDAO.class);
        bind(GaeUserDAO.class).in(Scopes.SINGLETON);
        bind(IOAuthProviderInfo.class).to(FacebookAuth.class);
        bind(ICreateDoc.class).toInstance(createDoc());
        bind(ShiroFilter.class).in(Scopes.SINGLETON);
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

}
