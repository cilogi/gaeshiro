// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        ServeContextListener.java  (25-Oct-2011)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.shiro.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.logging.Logger;


public class ServeContextListener extends GuiceServletContextListener {
    static final Logger LOG = Logger.getLogger(ServeContextListener.class.getName());


    private String userBaseUrl;

    public ServeContextListener() {
        userBaseUrl = "";
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)  {
        ServletContext context = servletContextEvent.getServletContext();
        if (context != null && context.getInitParameter("user-base-url") != null) {
            userBaseUrl = context.getInitParameter("user-base-url");
        }
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServeLogic(userBaseUrl), new ServeModule(userBaseUrl));
    }
    
}
