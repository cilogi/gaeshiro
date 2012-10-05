// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        FacebookAuth.java  (05-Oct-2012)
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


package com.cilogi.shiro.service;

import org.apache.shiro.web.util.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


public class FacebookAuth {
    static final Logger LOG = Logger.getLogger(FacebookAuth.class.getName());

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;

    private final String apiKey;
    private final String apiSecret;
    private final String host;

    public FacebookAuth() {
        Properties props = new Properties();
        loadProperties(props, "/fb.properties");
        apiKey = props.getProperty("fb.local.apiKey");
        apiSecret = props.getProperty("fb.local.apiSecret");
        host = props.getProperty("fb.local.host");
    }

    public String loginURL(String callbackUri) {
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback(makeAbsolute(callbackUri))
                                      .scope("email")
                                      .build();
        return service.getAuthorizationUrl(EMPTY_TOKEN);
    }

    private String makeAbsolute(String uri) {
        return uri.startsWith("/") ? host + uri : uri;
    }

    public JSONObject getUserInfo(String code, String callBackUrl) {
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback(makeAbsolute(callBackUrl))
                                      .scope("email")
                                      .build();
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        try {
            JSONObject obj =  new JSONObject(response.getBody());
            obj.put("access_token", accessToken.getToken());
            return obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static String logoutUrl(String redirect, String accessToken, ServletRequest request, ServletResponse response) throws IOException {
        String redirectOK = OAuthEncoder.encode(redirect);
        String logoutUrl = "https://www.facebook.com/logout.php?next="+redirectOK+"&access_token=" + accessToken;
        return logoutUrl;
    }

    private void loadProperties(Properties props, String resourceName) {
        try {
            props.load(getClass().getResourceAsStream(resourceName));
        } catch (IOException e) {
            LOG.severe("Can't load resource "+resourceName + ": " + e.getMessage());
        }
    }
}
