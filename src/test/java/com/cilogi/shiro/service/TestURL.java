// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        TestURL.java  (08-Oct-2012)
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


package com.cilogi.shiro.service;

import junit.framework.TestCase;

import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

public class TestURL extends TestCase {
    static final Logger LOG = Logger.getLogger(TestURL.class.getName());


    public TestURL(String nm) {
        super(nm);
    }

    @Override
    protected void setUp() {

    }

    public void testEscape() {
        try {
            URL url = new URL("http://example.com?query=one or more words");
            URI uri = new URI(
                    "http",
                    "example.com",
                    "/",
                    "query=one or more words&fred=wilma's husband",
                    null);
            String out = uri.toString();
            LOG.fine("ok");
        } catch (Exception e) {
            fail("Can't form URL: " + e.getMessage());
        }
    }
}