// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        TestMemcache.java  (26-Oct-2011)
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

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class TestMemcache extends TestCase {
    static final Logger LOG = Logger.getLogger(TestMemcache.class.getName());

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

    public TestMemcache(String nm) {
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

    public void testPutThenGet() {
        Memcache<String,String> cacheCars = new Memcache<String,String>("cars");
        Memcache<String,String> cachePlanes = new Memcache<String,String>("planes");

        cacheCars.putSync("audi", "a4");
        assertEquals("a4", cacheCars.get("audi"));

        cachePlanes.put("audi", "a4-flies");
        assertEquals("a4-flies", cachePlanes.get("audi"));
        assertEquals("a4", cacheCars.get("audi"));
    }
}