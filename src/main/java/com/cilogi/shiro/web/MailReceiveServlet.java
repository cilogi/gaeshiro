// Copyright (c) 2011 Tim Niblett. All Rights Reserved.
//
// File:        MailReceiveServlet.java  (20-Nov-2011)
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


import javax.inject.Singleton;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

@Singleton
public class MailReceiveServlet extends HttpServlet {
    static final Logger LOG = Logger.getLogger(MailReceiveServlet.class.getName());

    public MailReceiveServlet() {

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session, req.getInputStream());

            String subject = message.getSubject();

            LOG.info("Got an email. Subject = " + subject);

            String contentType = message.getContentType();
            LOG.info("Email Content Type : " + contentType);
        }
        catch (Exception ex) {
            LOG.warning("Failure in receiving email : " + ex.getMessage());
        }
    }
}
