// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUserCounter.java  (11-Nov-2011)
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

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This is a singleton, just holding a count of the number
 * of GaeUser objects we have.
 * <p> The reason for using this is that counting the number of items
 * dynamically is very inefficient, and costly.
 * <p> This counter should be changed relatively rarely (less than once a second)
 * so doesn't need to be sharded.
 */
@Cached
@Unindexed
class GaeUserCounter {
    static final Logger LOG = Logger.getLogger(GaeUserCounter.class.getName());

    static final long COUNTER_ID = 1L;

    @Id
    private long id;

    private int count;

    private Date lastModified;

    GaeUserCounter() {
        id = COUNTER_ID;
        lastModified = new Date(0L);
    }

    public int getCount() {
        return count;
    }

    public void delta(long delta) {
        this.count += delta;
        this.lastModified = new Date();
    }

    public Date getLastModified() {
        return lastModified;
    }
}
