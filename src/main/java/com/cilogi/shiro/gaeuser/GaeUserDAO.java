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


import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Logger;

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

    public void saveRegistration(String registrationString, String userName) {
        new RegistrationStringDAO().saveRegistration(registrationString, userName);
    }

    public String findUserNameFromValidCode(String code) {
        return new RegistrationStringDAO().findUserNameFromValidCode(code);
    }

    public GaeUser findUser(String userName) {
        return get(userName);
    }

    /**
     * Given a registration we have to retrieve it, and if its valid
     * update the associated user and then delete the registration.  This isn't
     * transactional and we may end up with a dangling RegistrationString, which
     * I can't see as too much of a problem, although they will need to be cleaned up with
     * a task on a regular basis (after they expire)..
     * @param code  The registration code
     * @param userName the user name for the code
     */
    public void register(final String code, final String userName) {
        GaeUser user = get(userName);
        if (user != null) {
            user.register();
            saveUser(user, true);
        }
        RegistrationStringDAO dao = new RegistrationStringDAO();
        RegistrationString reg = dao.get(code);
        if (reg != null) {
            dao.delete(code);
        }
    }
}
