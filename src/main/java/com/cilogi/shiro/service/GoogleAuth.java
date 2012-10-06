// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        GoogleAuth.java  (06-Oct-2012)
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

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


public class GoogleAuth {
    static final Logger LOG = Logger.getLogger(GoogleAuth.class.getName());

    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/userinfo/email?alt=json";
    private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    private static final Token EMPTY_TOKEN = null;

    private final String apiKey;
    private final String apiSecret;
    private final String host;

    @Inject
    public GoogleAuth(@Named("gg.property.prefix") String prefix) {
        LOG.info("prefix is " + prefix);
        Properties props = new Properties();
        loadProperties(props, "/social.properties");
        apiKey = props.getProperty(prefix + ".apiKey");
        apiSecret = props.getProperty(prefix + ".apiSecret");
        host = props.getProperty(prefix + ".host");
    }

    public String loginURL(String callbackUri) {
        OAuthService service = new ServiceBuilder()
                                      .provider(GoogleApi20.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback(makeAbsolute(callbackUri))
                                      .scope("https://www.googleapis.com/auth/userinfo.email")
                                      .build();
        return service.getAuthorizationUrl(EMPTY_TOKEN);
    }

    public String reAuthenticateURL(String callbackUri) {
        return loginURL(callbackUri)+"&approval_prompt=force";
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
        //Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        Token accessToken = getAccessToken(verifier, makeAbsolute(callBackUrl));
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

    public static String logoutUrl(String accessToken) throws IOException {
        String logoutUrl = "https://accounts.google.com/o/oauth2/revoke?token=" + accessToken;
        return logoutUrl;
    }

    private void loadProperties(Properties props, String resourceName) {
        try {
            props.load(getClass().getResourceAsStream(resourceName));
        } catch (IOException e) {
            LOG.severe("Can't load resource "+resourceName + ": " + e.getMessage());
        }
    }

    /**
     * This is hacked, as at the moment, the Scribe API hasn't moved up to Google's OAuth 2.0 implementation.
     * @param verifier verifier
     * @return  callback URL
     */
    public Token getAccessToken(Verifier verifier, String callbackUrl) {
        GoogleApi20 api = new GoogleApi20();
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://accounts.google.com/o/oauth2/token");
        request.addQuerystringParameter(OAuthConstants.CLIENT_ID, apiKey);
        request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, apiSecret);
        request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
        request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, callbackUrl);
        request.addQuerystringParameter(OAuthConstants.SCOPE, SCOPE);
        request.addQuerystringParameter("grant_type", "authorization_code"); // this is missing from Scribe
        Response response = request.send();
        return api.getAccessTokenExtractor().extract(response.getBody());
    }

}
