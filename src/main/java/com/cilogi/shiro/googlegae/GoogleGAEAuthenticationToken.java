// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        GoogleGAEAuthenticationToken.java  (16-Oct-2012)
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


package com.cilogi.shiro.googlegae;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

public class GoogleGAEAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {

    private final String principal;
    private final String host;

    public GoogleGAEAuthenticationToken(String principal, String host) {
        this.principal = principal;
        this.host = host;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public boolean isRememberMe() {
        return true;
    }

    @Override
    public String getHost() {
        return host;
    }
}
