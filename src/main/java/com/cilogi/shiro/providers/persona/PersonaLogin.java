// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        PersonaLogin.java  (10-Oct-2012)
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

import com.cilogi.shiro.gaeuser.IGaeUser;
import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

// Use 
public class PersonaLogin {

    private final IGaeUserDAO userDAO;

    @Inject
    public PersonaLogin(IGaeUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void login(PersonaAuthenticationToken personaToken) {
        personaToken.verify();
        loginWithNewSession(personaToken);
    }

    private void loginWithNewSession(PersonaAuthenticationToken token) {
        Preconditions.checkArgument(token.isValid(), "Token must be valid to allow login");

        Subject subject = SecurityUtils.getSubject();
        Session originalSession = subject.getSession();

        Map<Object, Object> attributes = sessionAttributes(originalSession);
        originalSession.stop();
        createNewUserIfNecessary(token);

        subject.login(token);

        Session newSession = subject.getSession();
        for(Object key : attributes.keySet() ) {
            newSession.setAttribute(key, attributes.get(key));
        }
    }

    // make sure that we have a user in the database with the email from this token.
    private void createNewUserIfNecessary(PersonaAuthenticationToken token) {
        Preconditions.checkNotNull(token, "Token can't be null when finding associated user");
        Preconditions.checkArgument(token.isValid(), "Token must be valid to allow user creation");

        String principal = (String)token.getPrincipal();
        IGaeUser user = userDAO.findUser(principal);
        if (user == null) {
            userDAO.ensureExists(principal);
        }
    }

    private Map<Object,Object> sessionAttributes(Session session) {
        Map<Object, Object> attributes = Maps.newLinkedHashMap();
        Collection<Object> keys = session.getAttributeKeys();
        for(Object key : keys) {
            Object value = session.getAttribute(key);
            if (value != null) {
                attributes.put(key, value);
            }
        }
        return attributes;
    }
}
