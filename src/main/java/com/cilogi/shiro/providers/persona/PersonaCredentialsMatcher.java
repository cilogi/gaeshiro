// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        OAuthCredentialsMatcher.java  (07-Oct-2012)
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
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersonaCredentialsMatcher implements CredentialsMatcher {
    static final Logger LOG = LoggerFactory.getLogger(PersonaCredentialsMatcher.class.getName());

    public PersonaCredentialsMatcher() {}

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        Preconditions.checkNotNull(info);
        Preconditions.checkNotNull(token);

        Object primary = info.getPrincipals().getPrimaryPrincipal();
        return token instanceof PersonaAuthenticationToken && token.getCredentials() != null && token.getPrincipal().equals(primary);
    }
}
