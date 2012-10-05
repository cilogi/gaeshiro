// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        MailQueueServlet.java  (01-Nov-2011)
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


package com.cilogi.shiro.web;

import com.cilogi.shiro.gae.UserDAO;
import com.cilogi.shiro.service.SendEmail;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Send registration emails via the task queue.  We need to use the task
 * queue as its possible for mail to take a long time and fail if we send it
 * within servlet requests.
 * <p> I'm using the default queue, which is limited to 5 requests per second,
 * which would need to change if this was other than a simple demo.
 */
@Singleton
public class MailQueueServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(MailQueueServlet.class.getName());

    private final SendEmail sendEmail;

    @Inject
    MailQueueServlet(Provider<UserDAO> daoProvider,  SendEmail sendEmail) {
        super(daoProvider);
        this.sendEmail = sendEmail;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(USERNAME);
        try {
            String registrationString = request.getParameter(REGISTRATION_STRING);
            boolean forgot = booleanParameter(FORGOT, request, false);
            String url = urlFor(request, registrationString, username, forgot);
            LOG.info("Link URL is " + url);
            String htmlMessage = forgot
                ? view("inc/registerEmail.ftl",
                        "email", username,
                        "href", url,
                        CODE, registrationString,
                        FORGOT, Boolean.toString(forgot))
                : view("inc/registerEmail.ftl",
                        "email", username,
                        "href", url,
                        CODE, registrationString);
            sendEmail.send(username, "Complete Registration for Shiro GAE", htmlMessage);
            LOG.info("Registration email sent to " + username + " with return url " + url);
        } catch (Exception e) {
            LOG.severe("Error sending mail to " + username + ": " + e.getMessage());
        }
    }


    private static String urlFor(HttpServletRequest request, String code, String userName, boolean forgot) {
        try {
            URI url = forgot
                ? new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(), "/registerByEmail.ftl",
                        "code="+code+"&username="+userName+"&forgot=true", null)
                : new URI(request.getScheme(), request.getServerName(), "/registerByEmail.ftl",
                        "code="+code+"&username="+userName, null);
            String s = url.toString();
            return s;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
