// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        InjectableIniRealm.java  (19/07/13)
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

import com.google.inject.Inject;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.text.IniRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InjectableIniRealm extends IniRealm {
    static final Logger LOG = LoggerFactory.getLogger(InjectableIniRealm.class);

    public InjectableIniRealm() {
        super();
    }
    @Inject
    public InjectableIniRealm(CredentialsMatcher matcher) {
        super();
        setCredentialsMatcher(matcher);
    }


    @Inject
    @Override
    public void setIni(Ini ini) {
        super.setIni(ini);
    }
}
