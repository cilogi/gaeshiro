// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        ViewMapping.java  (30/09/13)
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


package com.cilogi.web.servlets.shiro.view;

import com.cilogi.shiro.gaeuser.IGaeUser;
import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.shiro.gaeuser.impl.GaeUser;
import com.cilogi.shiro.providers.oauth.UserAuthType;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;


public class ViewMapping implements IViewMapping {
    static final Logger LOG = LoggerFactory.getLogger(ViewMapping.class);

    private static Map<String,Object> FMPP_MAP = fmppMap();

    @Getter
    protected IGaeUserDAO gaeUserDAO;


    @Inject
    public ViewMapping(IGaeUserDAO gaeUserDAO) {
        this.gaeUserDAO = gaeUserDAO;
    }

    @Override
    public Map<String,Object> baseMapping(HttpServletRequest request) {
        Map<String,Object> map = Maps.newHashMap();
        map.putAll(FMPP_MAP);

        IGaeUser user = getCurrentGaeUser();
        if (user != null) {
            map.put("userName", user.getName());
            map.put("userType", userType(user));
            map.put("userCSS", "shiro-user-active");
        } else {
            map.put("userName", "");
            map.put("userType", "UNKNOWN");
            map.put("userCSS", "shiro-guest-active");
        }
        map.put("RequestParameters", requestParameters(request));
        return map;
    }

    protected IGaeUser getCurrentGaeUser() {
        Subject subject = SecurityUtils.getSubject();
        String email = (String)subject.getPrincipal();
        if (email == null) {
            return null;
        } else {
            return gaeUserDAO.findUser(email);
        }
    }

    private static String userType(IGaeUser user) {
        return (user instanceof GaeUser) ? UserAuthType.CILOGI.name() : "SOCIAL";
    }

    private static Map<String,String> requestParameters(HttpServletRequest request) {
        Map<String,String> map = Maps.newHashMap();
        for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
            String key = (String)enumeration.nextElement();
            map.put(key, request.getParameter(key));
        }
        return map;
    }

    private static Map<String,Object> fmppMap() {
        final String PROP_FILE = "/ftl/data/const.properties";
        Map<String,Object> map = Maps.newHashMap();
        try (InputStream is = ViewMapping.class.getResourceAsStream(PROP_FILE)) {
            Properties props = new Properties();
            props.load(is);
            for (Object key : props.keySet()) {
                map.put((String)key, props.get(key));
            }
        } catch (IOException e) {
            LOG.warn("Can't load fmpp properties, " + PROP_FILE + ": " + e.getMessage());
        }
        return map;
    }
}
