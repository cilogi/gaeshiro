// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUser.java  (26-Oct-2011)
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


package com.cilogi.shiro.gaeuser.impl;

import com.cilogi.shiro.gaeuser.IGaeUser;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Cache
@Entity
public class GaeUser implements Serializable, IGaeUser {
    static final Logger LOG = Logger.getLogger(GaeUser.class.getName());


    private static final long serialVersionUID = 3118015798739037727L;


    @Id @Getter
    private String name;

    @Getter
    private Set<String> roles;

    @Getter
    private Set<String> permissions;


    @Index
    private Date dateRegistered;

    @Setter @Getter
    private boolean suspended;

    /** For objectify to create instances on retrieval */
    private GaeUser() {
        this.roles = new HashSet<String>();
        this.permissions = new HashSet<String>();
    }
    
    public GaeUser(String name) {
        this(name, Sets.newHashSet("user"), new HashSet<String>());
    }


    public GaeUser(String name, Set<String> roles, Set<String> permissions) {
        this(name, roles, permissions, false);
    }

    private GaeUser(String name, Set<String> roles, Set<String> permissions, boolean isRegistered) {
        Preconditions.checkNotNull(name, "User name (email) can't be null");
        Preconditions.checkNotNull(roles, "User roles can't be null");
        Preconditions.checkNotNull(permissions, "User permissions can't be null");
        this.name = name;

        this.roles = Collections.unmodifiableSet(roles);
        this.permissions = Collections.unmodifiableSet(permissions);
        this.dateRegistered = isRegistered ? new Date() : null;
        this.suspended = false;
    }

    @Override
    public Date getDateRegistered() {
        return dateRegistered == null ? null : new Date(dateRegistered.getTime());
    }

    public void register() {
        dateRegistered = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GaeUser) {
            GaeUser u = (GaeUser)o;
            return Objects.equal(getName(), u.getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
