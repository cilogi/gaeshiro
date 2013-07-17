// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        BaseDAO.java  (26/02/13)
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

import com.googlecode.objectify.Key;


import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

class BaseDAO<T> {
    static final Logger LOG = Logger.getLogger(BaseDAO.class.getName());

    private final Class clazz;

    BaseDAO(Class clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings({"unchecked"})
    T get(String id) {
        if (id == null || "".equals(id)) {
            return null;
        }
        T db = (T)ofy().load().key(Key.create(clazz, id)).now();
        return (db == null) ? newInstance(id) : db;
    }

    @SuppressWarnings({"unchecked"})
    private T newInstance(String id) {
        Constructor[] ctors = clazz.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getParameterTypes().length == 1 && ctor.getParameterTypes()[0].equals(String.class)) {
                try {
                    return (T)ctor.newInstance(id);
                } catch (Exception e) {
                    LOG.warning("Cannot construct instance of " + clazz.getName() + " with arg " + id + ": " + e.getMessage());
                    return null;
                }
            }
        }
        LOG.warning("Cannot construct instance of " + clazz.getName() + " as there are no single-arg constructors");
        return null;
    }


    void put(T object) {
        ofy().save().entity(object).now();
    }

    @SuppressWarnings({"unchecked"})
    void delete(String id) {
        ofy().delete().key(Key.create(clazz, id));
    }

}
