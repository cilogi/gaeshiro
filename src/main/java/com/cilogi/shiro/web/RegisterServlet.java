// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        RegisterServlet.java  (31-Oct-2011)
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

import com.cilogi.shiro.gae.GaeUser;
import com.cilogi.shiro.gae.UserDAO;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class RegisterServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(RegisterServlet.class.getName());

    private final String userBaseUrl;

    @Inject
    RegisterServlet(Provider<UserDAO> daoProvider, @Named("userBaseUrl") String userBaseUrl) {
        super(daoProvider);
        this.userBaseUrl = userBaseUrl;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);    
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserDAO dao = new UserDAO();

            String userName = WebUtils.getCleanParam(request, USERNAME);
            boolean isForgot = Boolean.parseBoolean(WebUtils.getCleanParam(request, FORGOT));

            GaeUser user = dao.findUser(userName);
            if (!isForgot && user != null && user.isRegistered()) {
                // You can't add a user who's already registered
                issueJson(response, HTTP_STATUS_FORBIDDEN,
                        MESSAGE, userName + " is already registered");
            }  else {
                // we override the user, any registration email should work though (as long as its valid)
                String registrationString = registrationString(userName);
                LOG.info("registration is " + registrationString);

                dao.saveRegistration(registrationString, userName);

                Queue queue = QueueFactory.getDefaultQueue();
                queue.add(TaskOptions.Builder
                        .withUrl(userBaseUrl + "/registermail")
                        .param(USERNAME, userName)
                        .param(FORGOT, Boolean.toString(isForgot))
                        .param(REGISTRATION_STRING, registrationString));

                issueJson(response, HTTP_STATUS_OK,
                        MESSAGE, "ok");
            }
        } catch (Exception e) {
            LOG.warning("Can't register: " + e.getMessage());
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_INTERNAL_SERVER_ERROR,
                  "Internal error: " + e.getMessage(), response);
        }
    }

    private String registrationString(String userName) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        ByteSource salt = rng.nextBytes();
        return new Sha256Hash(userName, new SimpleByteSource(salt), 63).toHex().substring(0,10);
    }
}
