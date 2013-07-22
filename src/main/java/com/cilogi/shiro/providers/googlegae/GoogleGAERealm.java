// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        GoogleGAERealm.java  (16-Oct-2012)
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


package com.cilogi.shiro.providers.googlegae;

import com.cilogi.shiro.memcache.MemcacheManager;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;

import java.util.logging.Logger;


public class GoogleGAERealm extends AuthenticatingRealm {
    static final Logger LOG = Logger.getLogger(GoogleGAERealm.class.getName());

    public GoogleGAERealm() {
        super(new MemcacheManager(), new GoogleGAECredentialsMatcher());
        setAuthenticationTokenClass(GoogleGAEAuthenticationToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token != null && token instanceof GoogleGAEAuthenticationToken) {
            GoogleGAEAuthenticationToken authToken = (GoogleGAEAuthenticationToken)token;
            return new GoogleGAEAuthenticationInfo((String)authToken.getPrincipal());
        } else {
            return null;
        }
    }
}
