// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUserRealm.java  (25-Oct-2011)
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

import com.cilogi.shiro.memcache.MemcacheManager;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

import javax.inject.Inject;
import java.util.logging.Logger;


public class GaeUserRealm extends AuthorizingRealm {
    private static final Logger LOG = Logger.getLogger(GaeUserRealm.class.getName());

    private GaeUserDAO gaeUserDAO;

    public GaeUserRealm() {
        super(new MemcacheManager(), PasswordHash.createCredentials());
        gaeUserDAO = new GaeUserDAO();
        LOG.fine("Creating a new instance of GaeUserRealm");
    }

    @Inject
    public GaeUserRealm(GaeUserDAO gaeUserDAO) {
        super(new MemcacheManager(), PasswordHash.createCredentials());
        this.gaeUserDAO = gaeUserDAO;
        LOG.fine("Creating a new instance of GaeUserRealm with injected GaeUserDAO");
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = ((UsernamePasswordToken) token).getUsername();
        return doGetAuthenticationInfo(userName);
    }

    private AuthenticationInfo doGetAuthenticationInfo(String userName) throws AuthenticationException {
        Preconditions.checkNotNull(userName, "User name can't be null");

        LOG.info("Finding authentication info for " + userName + " in DB");
        GaeUser user = gaeUserDAO.findUser(userName);


        if (user == null) {
            LOG.info("Rejecting " + userName + " because there is no user with that id");
            return null;
        }
        if (!user.isRegistered()) {
            LOG.info("Rejecting " + userName + " because the  user isn't registered");
            return null;
        }
        if (user.isSuspended()) {
            LOG.info("Rejecting " + userName + " because the user is suspended");
            return null;
        }
        LOG.info("Found " + userName + " in DB");

        SimpleAccount account = new SimpleAccount(user.getName(),
                user.getPasswordHash(), new SimpleByteSource(user.getSalt()), getName());
        account.setRoles(user.getRoles());
        account.setStringPermissions(user.getPermissions());
        return account;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Preconditions.checkNotNull(principals, "You can't have a null collection of principals");
        String userName = (String) getAvailablePrincipal(principals);
        if (userName == null) {
            throw new NullPointerException("Can't find a principal in the collection");
        }
        LOG.fine("Finding authorization info for " + userName + " in DB");
        GaeUser user = gaeUserDAO.findUser(userName);
        if (user == null || !user.isRegistered() || user.isSuspended()) {
            return null;
        }
        if (!user.isRegistered() || user.isSuspended()) {
            LOG.info("Can't authorize as user " + (user.isSuspended() ? " is suspended" : " is not registered"));
            return null;
        }
        LOG.fine("Found " + userName + " in DB");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(user.getRoles());
        info.setStringPermissions(user.getPermissions());
        return info;
    }
}
