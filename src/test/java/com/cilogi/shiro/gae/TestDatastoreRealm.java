// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        TestDatastoreRealm.java  (26-Oct-2011)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.shiro.gae;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import junit.framework.TestCase;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import java.util.logging.Logger;

public class TestDatastoreRealm extends TestCase {
    static final Logger LOG = Logger.getLogger(TestDatastoreRealm.class.getName());

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    public TestDatastoreRealm(String nm) {
        super(nm);
    }

    @Override
    protected void setUp() {
        helper.setUp();
    }

    @Override
    public void tearDown() {
        helper.tearDown();
    }


    public void testAuth() {
        GaeUserDAO dao = new GaeUserDAO();
        GaeUser user = new GaeUser("tim", "tim");
        user.register();
        dao.saveUser(user, true);

        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:com/cilogi/shiro/gae/shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject currentUser = SecurityUtils.getSubject();

        // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken("tim", "tim");
            try {
                currentUser.login(token);
                assertTrue(currentUser.isAuthenticated());
            } catch (Exception e) {
                fail("There is no user with username of " + token.getPrincipal());
            }
        }
    }
}