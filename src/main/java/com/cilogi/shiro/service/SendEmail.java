// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        SendEmail.java  (31-Oct-2011)
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


package com.cilogi.shiro.service;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.logging.Logger;


@Singleton
public class SendEmail {
    static final Logger LOG = Logger.getLogger(SendEmail.class.getName());

    private final String fromAddress;

    @Inject
    public SendEmail(@Named("email.from") String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void send(String toAddress, String title, String htmlMessage) {
        LOG.info("sending message to " + toAddress);
        MailService service = MailServiceFactory.getMailService();
        MailService.Message message = new MailService.Message();
        message.setSender(fromAddress);
        message.setTo(toAddress);
        message.setSubject(title);
        message.setHtmlBody(htmlMessage);
        try {
            service.send(message);
            LOG.info("message has been sent to " + toAddress);
        } catch (IOException e) {
            LOG.warning("Can't send email to " + toAddress + " about " + title + ": " + e.getMessage());
        }
    }
}
