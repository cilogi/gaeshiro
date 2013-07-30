// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        PersonaIniRealm.java  (14-Oct-2012)
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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.text.IniRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonaIniRealm extends IniRealm {
    static final Logger LOG = LoggerFactory.getLogger(PersonaIniRealm.class.getName());

    public PersonaIniRealm() {
        this("classpath:shiro.ini");
    }

    public PersonaIniRealm(String locationOfIni) {
        super(locationOfIni);
        setCredentialsMatcher(new PersonaCredentialsMatcher());
        setAuthenticationTokenClass(PersonaAuthenticationToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token != null && token instanceof PersonaAuthenticationToken) {
            return ((PersonaAuthenticationToken) token).doGetAuthenticationInfo();
        } else {
            return null;
        }
    }


    @Override
    protected void onInit() {}
}
