// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        JsonTokenExtractor.java  (08-Oct-2012)
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


package com.cilogi.shiro.providers.oauth.provider;

import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// here because the Scribe token extractor has an incorrect REGEX
class JsonTokenExtractor implements AccessTokenExtractor {
    static final Logger LOG = Logger.getLogger(JsonTokenExtractor.class.getName());

    private static final Pattern accessTokenPattern = Pattern.compile("\"access_token\"\\s*:\\s*\"(\\S*?)\"");

    JsonTokenExtractor() {}

    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");
        Matcher matcher = accessTokenPattern.matcher(response);
        if (matcher.find()) {
            return new Token(matcher.group(1), "", response);
        } else {
            throw new OAuthException("Cannot extract an access token. Response was: " + response);
        }
    }

}

