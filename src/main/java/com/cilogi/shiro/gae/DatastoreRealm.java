// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        DatastoreRealm.java  (25-Oct-2011)
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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

import java.util.logging.Logger;


public class DatastoreRealm extends AuthorizingRealm {
    private static final Logger LOG = Logger.getLogger(DatastoreRealm.class.getName());


    public DatastoreRealm() {
        super(new MemcacheManager(), theCredentials());

        LOG.fine("Creating a new instance of DatastoreRealm");
    }

    private UserDAO dao() {
        return UserDAOProvider.get();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = ((UsernamePasswordToken) token).getUsername();
        return doGetAuthenticationInfo(userName);
    }

    private AuthenticationInfo doGetAuthenticationInfo(String userName) throws AuthenticationException {
        Preconditions.checkNotNull(userName, "User name can't be null");

        LOG.fine("Finding authentication info for " + userName + " in DB");
        GaeUser user = dao().findUser(userName);

        boolean isGoogleUserPretendingToBeCilogi = false;
        User googleUser = UserServiceFactory.getUserService().getCurrentUser();
        if (user != null && googleUser != null && !googleUser.getEmail().equals(user.getName())) {
            isGoogleUserPretendingToBeCilogi = true;
        }

        if (user == null || userIsNotQualified(user) || isGoogleUserPretendingToBeCilogi) {
            return null;
        }
        LOG.fine("Found " + userName + " in DB");

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
        GaeUser user = dao().findUser(userName);
        if (user == null || userIsNotQualified(user)) {
            return null;
        }
        LOG.fine("Found " + userName + " in DB");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(user.getRoles());
        info.setStringPermissions(user.getPermissions());
        return info;
    }

    private static CredentialsMatcher theCredentials() {
        HashedCredentialsMatcher credentials = new HashedCredentialsMatcher(GaeUser.HASH_ALGORITHM);
        credentials.setHashIterations(GaeUser.HASH_ITERATIONS);
        credentials.setStoredCredentialsHexEncoded(true);
        return credentials;
    }

    private static boolean userIsNotQualified(GaeUser user) {
        return !user.isRegistered() || user.isSuspended();
    }
}
