// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        FreemarkerServlet.java  (07-Oct-2012)
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


package com.cilogi.shiro.web;

import com.cilogi.shiro.gaeuser.impl.GaeUser;
import com.cilogi.shiro.gaeuser.impl.GaeUserDAO;
import com.cilogi.shiro.gaeuser.IGaeUser;
import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.shiro.providers.oauth.UserAuthType;
import com.google.common.collect.Maps;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

//
//  I originally used FreemarkerServlet, but it didn't work with HTTPS for some reason.
//
@Singleton
public class FreemarkerServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(FreemarkerServlet.class.getName());

    @Inject
    public FreemarkerServlet(IGaeUserDAO gaeUserDAO) {
        super(gaeUserDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        showView(response, uri, mapping(request));
    }

    private Map<String,Object> mapping(HttpServletRequest request) {
        Map<String,Object> map = Maps.newHashMap();
        IGaeUser user = getCurrentGaeUser();
        if (user != null) {
            map.put("userName", user.getName());
            map.put("userType", userType(user));
        } else {
            map.put("userType", "UNKNOWN");
        }
        map.put("RequestParameters", requestParameters(request));
        return map;
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

}
