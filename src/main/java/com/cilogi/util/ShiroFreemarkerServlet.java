// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        ShiroFreemarkerServlet.java  (14-Nov-2011)
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


package com.cilogi.util;

import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Here so we can add common parameters to all the Freemarker files.
 */
@Singleton
public class ShiroFreemarkerServlet extends FreemarkerServlet {

    // The base URL for user admin commands, variable so we can
    // decide where we want it to be.
    private final String userBaseUrl;

    @Inject
    public ShiroFreemarkerServlet(@Named("userBaseUrl") String userBaseUrl) {
        this.userBaseUrl = userBaseUrl;
    }

    @Override
    protected boolean preTemplateProcess(
            HttpServletRequest request,
            HttpServletResponse response,
            Template template,
            TemplateModel data)
            throws ServletException, IOException {

        ((SimpleHash) data).put("userBaseUrl", userBaseUrl);
        return true;
    }
}
