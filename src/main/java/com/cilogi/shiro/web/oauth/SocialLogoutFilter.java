// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        SocialLogoutFilter.java  (04-Oct-2012)
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

package com.cilogi.shiro.web.oauth;

import com.cilogi.shiro.gae.GaeUser;
import com.cilogi.shiro.gae.UserDAO;
import com.cilogi.shiro.gae.UserDAOProvider;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.logging.Logger;

//import static com.cilogi.shiro.gae.UserAuthType.*;

public class SocialLogoutFilter extends LogoutFilter {
    static final Logger LOG = Logger.getLogger(SocialLogoutFilter.class.getName());

    @Inject
    public SocialLogoutFilter() {}

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        String principal = (String)subject.getPrincipal();

        try {
            subject.logout();
        } catch (SessionException ise) {
            LOG.info("Encountered session exception during logout.  This can generally safely be ignored: " + ise.getMessage());
        }

        GaeUser gaeUser = findUser(principal);
        if (gaeUser != null) {
            switch (gaeUser.getUserAuthType()) {
                case GOOGLE: WebUtil.logoutGoogle(request, response, "/");
                    break;
                case FACEBOOK: WebUtil.logoutFacebook(gaeUser, request, response);
                    break;
                default:
            }
        } else {
            WebUtils.issueRedirect(request, response, "/");
        }
        return false;
    }

    private static GaeUser findUser(String principal) {
        UserDAO dao = UserDAOProvider.get();
        return (principal == null) ? null : dao.findUser(principal);
    }
}
