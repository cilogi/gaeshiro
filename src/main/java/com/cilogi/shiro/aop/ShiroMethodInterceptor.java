// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        ShiroMethodInterceptor.java  (27-Oct-2011)
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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.logging.Logger;


public class ShiroMethodInterceptor implements MethodInterceptor {
    static final Logger LOG = Logger.getLogger(ShiroMethodInterceptor.class.getName());

    private org.apache.shiro.aop.MethodInterceptor methodInterceptor;

    public ShiroMethodInterceptor(org.apache.shiro.aop.MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }


	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		return methodInterceptor.invoke(new ShiroMethodInvocation(methodInvocation));
	}

	private static class ShiroMethodInvocation implements org.apache.shiro.aop.MethodInvocation {

		private final MethodInvocation methodInvocation;

		public ShiroMethodInvocation(MethodInvocation methodInvocation) {
			this.methodInvocation = methodInvocation;
		}

        @Override
        public Object proceed() throws Throwable {
            return methodInvocation.proceed();
        }

        @Override
        public Method getMethod() {
            return methodInvocation.getMethod();
        }

        @Override
        public Object[] getArguments() {
            return methodInvocation.getArguments();
        }

        @Override
        public Object getThis() {
            return methodInvocation.getThis();
        }
    }
}
