// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        UserDAOProvider.java  (04-Oct-2012)
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


package com.cilogi.shiro.gae;

import java.util.logging.Logger;

/**
 * The UserDAO object is not thread-safe as the underlying objectify instance
 * isn't thread-safe.  So, we provide a per thread DAO, which by construction will
 * be thread safe.
 * <p>This makes sense, I think, as a single thread will last at least one request,
 * and quite likely many more if the container does thread management.  Keeping
 * hold of the thread allows more caching, as the objectify instance caches. 
 */
public class UserDAOProvider {
    static final Logger LOG = Logger.getLogger(UserDAOProvider.class.getName());

    private static final ThreadLocal<UserDAO> tlDao = new ThreadLocal<UserDAO>() {
        protected UserDAO initialValue() {
            UserDAO dao = new UserDAO();
            return dao;
        }
    };

    public static UserDAO get() {
        return tlDao.get();
    }


    private UserDAOProvider() {}
}
