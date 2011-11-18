// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        AopModule.java  (27-Oct-2011)
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


package com.cilogi.shiro.aop;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.authz.aop.*;

/**
 * Allow the use of Shiro annotations within Guice.  Just include this module and
 * you can add annotations to methods.
 */
public class AopModule extends AbstractModule {

    public AopModule() {
        super();
    }

    @Override
    public void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresRoles.class),
                new ShiroMethodInterceptor(new RoleAnnotationMethodInterceptor()));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresUser.class),
                new ShiroMethodInterceptor(new UserAnnotationMethodInterceptor()));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresPermissions.class),
                new ShiroMethodInterceptor(new PermissionAnnotationMethodInterceptor()));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresGuest.class),
                new ShiroMethodInterceptor(new GuestAnnotationMethodInterceptor()));
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(RequiresAuthentication.class),
                new ShiroMethodInterceptor(new AuthenticatedAnnotationMethodInterceptor()));
    }

}
