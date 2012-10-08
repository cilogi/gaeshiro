// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        AuthBase.java  (08-Oct-2012)
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


package com.cilogi.shiro.oauth.provider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


abstract class AuthBase {
    static final Logger LOG = Logger.getLogger(AuthBase.class.getName());

    private String state;

    AuthBase() {
        state = "random string";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    protected void loadProperties(Properties props, String resourceName) {
        try {
            props.load(getClass().getResourceAsStream(resourceName));
        } catch (IOException e) {
            LOG.severe("Can't load resource "+resourceName + ": " + e.getMessage());
        }
    }

    protected String makeAbsolute(String uri, String host) {
        return uri.startsWith("/") ? host + uri : uri;
    }

    protected String errorString(JSONObject obj) {
        if (obj.has("error")) {
            try {
                JSONObject errObj = obj.getJSONObject("error");
                String message = errObj.getString("message");
                return (message == null) ? "unknown JSON error, no message field found" : message;
            } catch (JSONException e) {
                return "Unknown error, JSON won't parse: " + obj.toString();
            }
        } else {
            return null;
        }
    }
}
