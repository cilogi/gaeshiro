// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        OAuthAuthenticationToken.java  (07-Oct-2012)
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


package com.cilogi.shiro.oauth;

import com.cilogi.shiro.gae.UserAuthType;
import com.google.common.base.Preconditions;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

import java.util.logging.Logger;


public class OAuthAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {
    static final Logger LOG = Logger.getLogger(OAuthAuthenticationToken.class.getName());

    private final String token;
    private final UserAuthType authType;
    private final String principal;
    private final String host;

    public OAuthAuthenticationToken(String token, UserAuthType authType, String principal, String host) {
        Preconditions.checkNotNull(token, "You have to have an OAuth token to create an authentication token");
        Preconditions.checkNotNull(authType, "You have to specify an OAuth type to create an authentication token");
        Preconditions.checkNotNull(principal, "You have to have a principal (email address) to create an authentication token");

        this.token = token;
        this.authType = authType;
        this.principal = principal;
        this.host = host;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public boolean isRememberMe() {
        return true;
    }

    @Override
    public String getHost() {
        return host;
    }

    public UserAuthType getAuthType() {
        return authType;
    }
}
