// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        OAuthInfo.java  (08-Oct-2012)
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


package com.cilogi.shiro.oauth;

import com.cilogi.shiro.gae.UserAuthType;
import com.google.common.base.Preconditions;

import java.util.logging.Logger;

// The info we store when we perform a successful OAuth request with a token.
public class OAuthInfo {
    static final Logger LOG = Logger.getLogger(OAuthInfo.class.getName());

    private final String token;
    private final String email;
    private final String errorString;
    private final UserAuthType userAuthType;

    private OAuthInfo(String token, String email, String errorString, UserAuthType userAuthType) {
        this.token = token;
        this.email = email;
        this.errorString = errorString;
        this.userAuthType = userAuthType;
    }

    public boolean isError() {
        return errorString != null;
    }

    public UserAuthType getUserAuthType() {
        return userAuthType;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getErrorString() {
        return errorString;
    }

    public static class Builder {
        private String token;
        private String email;
        private String errorString;
        private UserAuthType authType;

        public Builder(UserAuthType authType) {
            this.authType = authType;
        }
        public Builder token(String token) { this.token = token; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder errorString(String errorString) { this.errorString = errorString; return this; }

        public OAuthInfo build() {
            if (errorString == null) {
                Preconditions.checkNotNull(token, "You cannot have an empty token when there are no errors");
                Preconditions.checkNotNull(email, "You cannot have an empty email address when there are no errors");
                Preconditions.checkNotNull(authType, "You cannot have a null auth type");
            }
            return new OAuthInfo(token, email, errorString, authType);
        }
    }
}
