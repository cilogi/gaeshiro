// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        PasswordHash.java  (11/07/13)
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


package com.cilogi.shiro.util;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Details of the password hashing.  The number of iterations, algorithm, and so on.
 */
public class PasswordHash {
    private PasswordHash() {}

    private static final int HASH_ITERATIONS = 1;
    private static final String HASH_ALGORITHM = Sha256Hash.ALGORITHM_NAME;

    public static String hash(String password, byte[] salt) {
        return (password == null) ? null : new Sha256Hash(password, new SimpleByteSource(salt), HASH_ITERATIONS).toHex();
    }

    public static CredentialsMatcher createCredentials() {
        HashedCredentialsMatcher credentials = new HashedCredentialsMatcher(HASH_ALGORITHM);
        credentials.setHashIterations(HASH_ITERATIONS);
        credentials.setStoredCredentialsHexEncoded(true);
        return credentials;
    }
}
