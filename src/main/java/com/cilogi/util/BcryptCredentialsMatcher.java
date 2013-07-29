// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        BCryptCredentialsMatcher.java  (26/07/13)
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


package com.cilogi.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.mindrot.jbcrypt.BCrypt;


/**
 * Check credentials generated with BCrypt.  Not used at present, but probably should be
 * with all the NSA scares around.
 */
@Slf4j
public class BcryptCredentialsMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if(token instanceof UsernamePasswordToken) {
            String password = toString(((UsernamePasswordToken) token).getPassword());
            String hashed_password = getCredentials(info);
            return BCrypt.checkpw(password, hashed_password);
        } else {
            log.warn("Token is not a UsernamePasswordToken: ", token);
            return false;
        }
    }

    private String getCredentials(AuthenticationInfo info) {

        Object credentials = info.getCredentials();
        return toString(credentials);
    }

    private String toString(Object o) {
        if (o == null) {
            String msg = "Argument for String conversion cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        if (o instanceof byte[]) {
            return toString((byte[]) o);
        } else if (o instanceof char[]) {
            return new String((char[]) o);
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return o.toString();
        }
    }
}
