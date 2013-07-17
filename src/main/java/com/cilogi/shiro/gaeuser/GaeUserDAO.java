// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUserDAO.java  (01-Nov-2011)
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


package com.cilogi.shiro.gaeuser;


import com.google.common.collect.Sets;
import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class GaeUserDAO extends BaseDAO<GaeUser> {
    static final Logger LOG = Logger.getLogger(GaeUserDAO.class.getName());
    

    static {
        ObjectifyService.register(GaeUser.class);
    }

    public GaeUserDAO() {
        super(GaeUser.class);
    }

    public long getCount() {
        return new UserCounterDAO().getCount();
    }

    /**
     * Save user with authorization information
     * @param user  User
     * @param changeCount should the user count be incremented
     * @return the user, after changes
     */
    public GaeUser saveUser(GaeUser user, boolean changeCount) {
        super.put(user);
        if (changeCount) {
            new UserCounterDAO().changeCount(1L);
        }
        return user;
    }

    public GaeUser deleteUser(GaeUser user) {
        super.delete(user.getName());
        new UserCounterDAO().changeCount(-1L);
        return user;
    }

    public GaeUser findUserFromValidCode(String code) {
        GaeUser user = ofy().load().type(GaeUser.class).filter("registrationString.code", code).first().now();
        return user;
    }

    public GaeUser findUser(String userName) {
        return get(userName);
    }

    /**
     * Register a user, clearing out the registration string (if any)
     * @param user  The user
     */
    public void register(GaeUser user) {
        if (user != null) {
            user.register();
            user.setRegistrationString(null);
            saveUser(user, true);
        }
    }

    /**
     * Make sure that there is a database entry for an email address
     * and register it, if it doesn't exist.
     * @param email The email for which the entry is required.
     */
    public GaeUser ensureExists(final String email) {
        GaeUser user = findUser(email);
        if (user == null) {
            user = new GaeUser(email, Sets.newHashSet("user"), Sets.<String>newHashSet());
            user.register();
            saveUser(user, true);
        }
        return user;
    }
}
