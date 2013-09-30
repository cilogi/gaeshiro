// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        RenderView.java  (30/09/13)
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

import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.util.MimeTypes;
import com.cilogi.util.doc.ICreateDoc;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class RenderView implements IRenderView {
    static final Logger LOG = LoggerFactory.getLogger(RenderView.class);

    private final int HTTP_STATUS_OK = 200;

    private final ICreateDoc create;
    private final IViewMapping viewMapping;

    @Inject
    public RenderView(ICreateDoc create, IGaeUserDAO gaeUserDAO) {
        this.create = create;
        this.viewMapping = new ViewMapping(gaeUserDAO);
    }

    @Override
    public void defaultRender(HttpServletRequest request, HttpServletResponse response, String name) throws IOException {
        render(request, response, name, viewMapping.baseMapping(request));

    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, String name, Map<String,Object> bindings) throws IOException {
        String html =  new String(create.createDocument(name, bindings), Charsets.UTF_8);
        issue(MimeTypes.MIME_TEXT_HTML, HTTP_STATUS_OK, html, response);

    }

    private static  void issue(String mimeType, int returnCode, String output, HttpServletResponse response) throws IOException {
        response.setContentType(mimeType);
        response.setStatus(returnCode);
        response.getWriter().println(output);
    }
}
