// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        GoogleGAEAuthenticationInfo.java  (16-Oct-2012)
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

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import java.util.logging.Logger;


public class GoogleGAEAuthenticationInfo implements AuthenticationInfo {
    static final Logger LOG = Logger.getLogger(GoogleGAEAuthenticationInfo.class.getName());

    private final String principal;

    public GoogleGAEAuthenticationInfo(String principal) {
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public PrincipalCollection getPrincipals() {
        return new SimplePrincipalCollection(principal, "GoogleGAE");
    }

}
