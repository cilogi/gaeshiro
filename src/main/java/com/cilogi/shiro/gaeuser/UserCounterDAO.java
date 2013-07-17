// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        UserCounterDAO.java  (09/07/13)
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


package com.cilogi.shiro.gaeuser;

import com.googlecode.objectify.ObjectifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


class UserCounterDAO extends BaseDAO<UserCounter> {
    static final Logger LOG = LoggerFactory.getLogger(UserCounterDAO.class);

    static {
        ObjectifyService.register(UserCounter.class);
    }

    public UserCounterDAO() {
        super(UserCounter.class);
    }


    long getCount() {
        UserCounter count = get(UserCounter.COUNTER_ID);
        return (count == null) ? 0 : count.getCount();
    }

    Date getCountLastModified() {
        UserCounter count = get(UserCounter.COUNTER_ID);
        return (count == null) ? new Date(0L) : count.getLastModified();
    }

    /**
     * Change the user count.
     * @param delta amount to change
     */
    void changeCount(final long delta) {
        UserCounter count = get(UserCounter.COUNTER_ID);
        if (count == null) {
            count = new UserCounter(UserCounter.COUNTER_ID);
        }
        count.delta(delta);
        put(count);
    }
}
