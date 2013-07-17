// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        RegistrationString.java  (01-Nov-2011)
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

import com.googlecode.objectify.annotation.*;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Embed
public class RegistrationString {

    @Index
    private String code;

    private long expires;

    // for objectify
    private RegistrationString() {}

    public RegistrationString(String userName, long validity, TimeUnit unit) {
        this.code = generate(userName);
        long now = new Date().getTime();
        this.expires = now + unit.toMillis(validity);
    }

    public String getCode() {
        return code;
    }

    public boolean isValid() {
        return expires > new Date().getTime();
    }

    private static String generate(String userName) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        ByteSource salt = rng.nextBytes();
        return new Sha256Hash(userName, new SimpleByteSource(salt), 63).toHex().substring(0,10);
    }
}
