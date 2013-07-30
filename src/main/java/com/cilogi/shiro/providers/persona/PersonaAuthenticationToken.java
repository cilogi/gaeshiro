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


package com.cilogi.shiro.providers.persona;

import com.google.common.base.Preconditions;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class PersonaAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {
    static final Logger LOG = LoggerFactory.getLogger(PersonaAuthenticationToken.class);

    private final String token;
    private String principal;
    private final String host;
    private final boolean isRememberMe;

    public PersonaAuthenticationToken(String token, String host, boolean isRememberMe) {
        Preconditions.checkNotNull(token, "You have to have a Persona token to create an authentication token");

        this.token = token;
        this.principal = null;
        this.host = host;
        this.isRememberMe = isRememberMe;
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
        return isRememberMe;
    }

    @Override
    public String getHost() {
        return host;
    }
    
    public boolean isValid() {
        return principal != null;
    }

    public void verify() throws AuthenticationException {
        PersonaVerifier verifier = new PersonaVerifier();
        Map<String,String> map = verifier.verify(token, host);
        principal = map.get(PersonaVerifier.EMAIL_FIELD);
        if (principal == null) {
            throw new AuthenticationException("Can't verify token: " + token);
        }
    }

    public AuthenticationInfo doGetAuthenticationInfo() throws AuthenticationException {
        String credentials = (String)getCredentials();
        String principal = (String)getPrincipal();
        if (credentials == null || principal == null) {
            throw new AuthenticationException("Both credential (" + credentials + ") and principal " +
                    principal + ") must be non-null for a token to authenticate");
        }
        return new PersonaAuthenticationInfo(credentials, principal);
    }

}
