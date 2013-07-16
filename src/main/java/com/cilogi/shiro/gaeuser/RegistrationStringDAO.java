// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        RegistrationStringDAO.java  (09/07/13)
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

import com.googlecode.objectify.ObjectifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


class RegistrationStringDAO extends BaseDAO<RegistrationString> {
    static final Logger LOG = LoggerFactory.getLogger(RegistrationStringDAO.class);

    private static final long REGISTRATION_VALID_DAYS = 1;

    static {
        ObjectifyService.register(RegistrationString.class);
    }

    RegistrationStringDAO() {
        super(RegistrationString.class);
    }

    RegistrationString saveRegistration(String registrationString, String userName) {
        RegistrationString reg = new RegistrationString(registrationString, userName, REGISTRATION_VALID_DAYS, TimeUnit.DAYS);
        put(reg);
        return reg;
    }

    String findUserNameFromValidCode(String code) {
        RegistrationString reg = get(code);
        return (reg == null) ?  null : (reg.isValid() ? reg.getUsername() : null);
    }
}
