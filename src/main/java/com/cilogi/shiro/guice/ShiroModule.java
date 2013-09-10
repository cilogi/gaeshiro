// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        ShiroModule.java  (19/07/13)
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


package com.cilogi.shiro.guice;

import com.cilogi.shiro.memcache.MemcacheManager;
import com.cilogi.shiro.providers.googlegae.GoogleGAERealm;
import com.cilogi.shiro.providers.oauth.OAuthRealm;
import com.cilogi.shiro.providers.persona.PersonaRealm;
import com.cilogi.shiro.web.appengine.GoogleLogoutFilter;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

public class ShiroModule extends ShiroWebModule{
    static final Logger LOG = LoggerFactory.getLogger(ShiroModule.class);

    public static final Key<GoogleLogoutFilter> GOOGLE_LOGOUT_FILTER = Key.get(GoogleLogoutFilter.class);

    public ShiroModule(ServletContext sc) {
        super(sc);
    }

    @SuppressWarnings("unchecked")
    protected void configureShiroWeb() {
        try {
            bindRealm().toConstructor(IniRealm.class.getConstructor()).in(Scopes.SINGLETON);
            bindRealm().to(OAuthRealm.class).in(Scopes.SINGLETON);
            bindRealm().to(GoogleGAERealm.class).in(Scopes.SINGLETON);
            bindRealm().to(PersonaRealm.class).in(Scopes.SINGLETON);
            bind(CredentialsMatcher.class).to(PasswordMatcher.class);
            bind(CacheManager.class).to(MemcacheManager.class);
            bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login");
        } catch (NoSuchMethodException e) {
            addError(e);
        }

        addFilterChain("/login", AUTHC_BASIC);
        addFilterChain("/settings.ftl", AUTHC_BASIC);
        addFilterChain("/listUsers.ftl", AUTHC_BASIC, config(ROLES, "admin"));
        addFilterChain("/logout", GOOGLE_LOGOUT_FILTER);
    }

    @Provides
    Ini loadShiroIni() {
        return Ini.fromResourcePath("classpath:shiro.ini");
    }
}
