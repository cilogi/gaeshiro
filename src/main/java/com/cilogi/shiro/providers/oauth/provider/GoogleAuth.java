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


package com.cilogi.shiro.providers.oauth.provider;

import com.cilogi.shiro.providers.oauth.OAuthInfo;
import com.cilogi.shiro.providers.oauth.UserAuthType;
import com.google.appengine.api.urlfetch.*;
import com.google.common.base.Charsets;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;


public class GoogleAuth extends AuthBase implements IOAuthProviderInfo {
    static final Logger LOG = Logger.getLogger(GoogleAuth.class.getName());

    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/userinfo/email?alt=json";
    private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    private static final Token EMPTY_TOKEN = null;

    private final String apiKey;
    private final String apiSecret;
    private final String host;

    @Inject
    public GoogleAuth(@Named("social.site") String site) {
        LOG.info("site is " + site);
        String prefix = "gg." + site;
        Properties props = new Properties();
        loadProperties(props, "/social.properties");
        apiKey = props.getProperty(prefix + ".apiKey");
        apiSecret = props.getProperty(prefix + ".apiSecret");
        host = props.getProperty(prefix + ".host");
    }

    @Override
    public UserAuthType getUserAuthType() {
        return UserAuthType.GOOGLE;
    }

    @Override
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

    @Override
    public String reAuthenticateURL(String callbackUri) {
        return loginURL(callbackUri)+"&approval_prompt=force";
    }

    @Override
    public OAuthInfo getUserInfo(String code, String callBackUrl) {
        JSONObject obj = getUserInfoJSON(code, callBackUrl);
        try {
            JSONObject data = obj.getJSONObject("data");
            return new OAuthInfo.Builder(UserAuthType.GOOGLE)
                    .errorString(errorString(obj))
                    .email(data.optString("email"))
                    .token(obj.optString("access_token"))
                    .build();
        } catch (JSONException e) {
            LOG.warning("Can't parse Google's return info: " + obj.toString());
            return null;
        }
    }


    private String makeAbsolute(String uri) {
        return uri.startsWith("/") ? host + uri : uri;
    }

    private JSONObject getUserInfoJSON(String code, String callBackUrl) {
        OAuthService service = new ServiceBuilder()
                                      .provider(GoogleApi20.class)
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

    @Override
    public void revokeToken(String token, HttpServletRequest request, HttpServletResponse response,
                            String redirectURL)  throws IOException {
        String redirectHome = response.encodeRedirectURL(redirectURL);
        String url = GoogleAuth.logoutUrl(token);
        String reply = fetch(new URL(url));
        if (reply != null) {
            LOG.info("Failed to logout from Google: " + reply);
        }
        WebUtils.issueRedirect(request, response, redirectHome);
    }


    /**
     * This is hacked, as at the moment, the Scribe API hasn't moved up to Google's OAuth 2.0 implementation.
     * @param verifier verifier
     * @param callbackUrl callback
     * @return  callback URL
     */
    public Token getAccessToken(Verifier verifier, String callbackUrl) {
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://accounts.google.com/o/oauth2/token");
        request.addBodyParameter(OAuthConstants.CLIENT_ID, apiKey);
        request.addBodyParameter(OAuthConstants.CLIENT_SECRET, apiSecret);
        request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
        request.addBodyParameter(OAuthConstants.REDIRECT_URI, callbackUrl);
        //request.addBodyParameter(OAuthConstants.SCOPE, SCOPE);
        request.addBodyParameter("grant_type", "authorization_code"); // this is missing from Scribe
        Response response = request.send();
        return new JsonTokenExtractor().extract(response.getBody());
    }

    private static String fetch(URL url) {
        URLFetchService service = URLFetchServiceFactory.getURLFetchService();
        try {
            HTTPRequest request = new HTTPRequest(url, HTTPMethod.POST);
            HTTPResponse response = service.fetch(request);
            if (response.getResponseCode() == 200) {
                return null;
            } else {
                String s = new String(response.getContent(), Charsets.UTF_8);
                return (s == null) ? "Unknown error with code " + response.getResponseCode() : s;
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

}
