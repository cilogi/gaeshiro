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


package com.cilogi.shiro.gae;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import static com.cilogi.shiro.gae.UserAuthType.*;

@Cached
@Unindexed
public class GaeUser implements Serializable {
    static final Logger LOG = Logger.getLogger(GaeUser.class.getName());

    static final int HASH_ITERATIONS = 1;
    static final String HASH_ALGORITHM = Sha256Hash.ALGORITHM_NAME;


    @Id
    private String name;

    private String passwordHash;

    /** The salt, used to make sure that a dictionary attack is harder given a list of all the
     *  hashed passwords, as each salt will be different.
     */
    private byte[] salt;

    private Set<String> roles;

    private Set<String> permissions;

    @Indexed
    private Date dateRegistered;

    private boolean isSuspended;

    private UserAuthType userAuthType;

    /** For objectify to create instances on retrieval */
    private GaeUser() {
        this.roles = new HashSet<String>();
        this.permissions = new HashSet<String>();
        this.userAuthType = CILOGI;
    }
    
    GaeUser(String name, String password) {
        this(name, password, new HashSet<String>(), new HashSet<String>());
    }
    
    public GaeUser(String name, String password, Set<String> roles, Set<String> permissions) {
        this(name, password, roles, permissions, false);
    }

    GaeUser(String name, String password, Set<String> roles, Set<String> permissions, boolean isRegistered) {
        Preconditions.checkNotNull(name, "User name (email) can't be null");
        Preconditions.checkNotNull(password, "User password can't be null");
        Preconditions.checkNotNull(roles, "User roles can't be null");
        Preconditions.checkNotNull(permissions, "User permissions can't be null");
        this.name = name;

        this.salt = salt().getBytes();
        this.passwordHash = hash(password, salt);
        this.roles = Collections.unmodifiableSet(roles);
        this.permissions = Collections.unmodifiableSet(permissions);
        this.dateRegistered = isRegistered ? new Date() : null;
        this.isSuspended = false;
        this.userAuthType = CILOGI;
    }

    public UserAuthType getUserAuthType() {
        return userAuthType;
    }

    public void setUserAuthType(UserAuthType userAuthType) {
        Preconditions.checkNotNull(userAuthType, "The auth type of a user cannot be null");
        this.userAuthType = userAuthType;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public void setPassword(String password) {
        Preconditions.checkNotNull(password);
        this.salt = salt().getBytes();
        this.passwordHash = hash(password, salt);
    }

    public Date getDateRegistered() {
        return dateRegistered == null ? null : new Date(dateRegistered.getTime());
    }

    public boolean isRegistered() {
        return getDateRegistered() != null;
    }

    public void register() {
        dateRegistered = new Date();
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    private static ByteSource salt() {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        return rng.nextBytes();
    }

    private static String hash(String password, byte[] salt) {
        return new Sha256Hash(password, new SimpleByteSource(salt), HASH_ITERATIONS).toHex();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GaeUser) {
            GaeUser u = (GaeUser)o;
            return getName().equals(u.getName())
                    && getPasswordHash().equals(u.getPasswordHash());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + passwordHash.hashCode();
    }
}
