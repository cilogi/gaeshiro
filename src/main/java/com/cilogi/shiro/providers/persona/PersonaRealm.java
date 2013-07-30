// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        PersonaRealm.java  (10-Oct-2012)
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


package com.cilogi.shiro.providers.persona;

import com.cilogi.shiro.gaeuser.IGaeUser;
import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.google.common.base.Preconditions;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


public class PersonaRealm extends AuthorizingRealm {

    private final IGaeUserDAO gaeUserDAO;

    protected PersonaRealm(IGaeUserDAO gaeUserDAO, CacheManager cacheManager) {
        super(cacheManager, new PersonaCredentialsMatcher());
        this.gaeUserDAO = gaeUserDAO;
        setAuthenticationTokenClass(PersonaAuthenticationToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token != null && token instanceof PersonaAuthenticationToken) {
            return ((PersonaAuthenticationToken) token).doGetAuthenticationInfo();
        } else {
            return null;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Preconditions.checkNotNull(principals, "You can't have a null collection of principals");
        String principal = (String) getAvailablePrincipal(principals);
        if (principal == null) {
            throw new NullPointerException("Can't find a principal in the collection");
        }
        IGaeUser user = gaeUserDAO.findUser(principal);
        if (user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(user.getRoles());
            info.setStringPermissions(user.getPermissions());
            return info;
        } else {
            return null;
        }
    }

}
