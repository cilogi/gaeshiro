// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        PersonaVerifier.java  (10-Oct-2012)
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


package com.cilogi.shiro.providers.persona;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


class PersonaVerifier {
    static final Logger LOG = LoggerFactory.getLogger(PersonaVerifier.class);

    static final String VERIFY_URL = "https://verifier.login.persona.org/verify";
    static final String STATUS_FIELD = "status";
    static final String EMAIL_FIELD = "email";

    static final String TOKEN_OK = "okay";
    static final String TOKEN_FAILURE = "failure";

    PersonaVerifier() {}

    Map<String,String> verify(String personaToken, String host) {
        Map<String,String> map = Maps.newHashMap();                               
        try {
            URL url = new URL(String.format(VERIFY_URL + "?assertion=%s&audience=%s",
                                            encode(personaToken), encode(host)));
            String s = postURL(url);
            JSONObject obj = new JSONObject(s);
            map.put(STATUS_FIELD, obj.optString(STATUS_FIELD, TOKEN_FAILURE));
            if (map.get(STATUS_FIELD).equals(TOKEN_OK)) {
                map.put(EMAIL_FIELD, obj.getString(EMAIL_FIELD));
            }
        } catch (Exception e) {
            LOG.info("Verification of token failed: " + e.getMessage());
        }
        return map;

    }

    static String encode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Error encoding: " + s + ": " + e.getMessage());
            return s;
        }
    }

    static String postURL(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.close();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = connection.getInputStream();
            byte[] data = copyStream(is);
            is.close();
            return new String(data, Charsets.UTF_8);
        } else {
            return null;
        }

    }

    static byte[] copyStream(InputStream in) throws IOException {
        return ByteStreams.toByteArray(in);
    }
}
