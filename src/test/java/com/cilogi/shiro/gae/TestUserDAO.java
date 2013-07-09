// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        TestUserDAO.java  (02-Nov-2011)
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

import java.util.logging.Logger;


public class TestUserDAO extends TestCase {
    static final Logger LOG = Logger.getLogger(TestUserDAO.class.getName());

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    public TestUserDAO(String nm) {
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

    public void testBase() {
        UserDAO dao = new UserDAO();
        long startCount = dao.getCount();
        GaeUser user = new GaeUser("tim", "tim");
        user.register();
        dao.saveUser(user, true);
        GaeUser back = dao.findUser("tim");
        assertEquals(user, back);
        assertEquals(1L + startCount, dao.getCount());
    }

}