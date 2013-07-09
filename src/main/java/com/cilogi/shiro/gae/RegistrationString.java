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


package com.cilogi.shiro.gae;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Cache
@Entity
class RegistrationString {
    static final Logger LOG = Logger.getLogger(RegistrationString.class.getName());

    @Id
    private String registrationString;

    private String username;
    private Date dateCreated;
    private long validityMilliseconds;

    // for objectify
    private RegistrationString() {}

    RegistrationString(String registrationString, String username, long amount, TimeUnit unit) {
        this.registrationString = registrationString;
        this.username = username;
        this.dateCreated = new Date();
        this.validityMilliseconds = unit.toMillis(amount);
    }

    String getRegistrationString() {
        return registrationString;
    }

    String getUsername() {
        return username;
    }

    boolean isValid() {
        return dateCreated.getTime() + validityMilliseconds > new Date().getTime();
    }
}
