// Copyright (c) 2011 Tim Niblett. All Rights Reserved.
//
// File:        ParameterNames.java  (17-Nov-2011)
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

/**
 * These are the parameters used to post information from the
 * client to the server.  Gathered here for easy reference.
 */
interface ParameterNames {
    String CODE = "code";

    // these 4 are from jQuery.dataTables
    String DATATABLE_ECHO = "sEcho";
    String DATATABLE_START = "iDisplayStart";
    String DATATABLE_LENGTH = "iDisplayLength";
    String DATATABLE_SEARCH = "sSearch";

    String DELETE = "delete";
    String FORGOT = "forgot";
    String INVALIDATE_CACHE = "invalidateCache";
    String PASSWORD = "password";
    String REGISTRATION_STRING = "registrationString";
    String REMEMBER_ME = "rememberMe";
    String SUSPEND = "suspend";
    String USERNAME = "username";
}