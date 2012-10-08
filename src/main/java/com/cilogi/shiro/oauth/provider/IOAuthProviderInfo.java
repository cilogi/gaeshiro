// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        IOAuthProviderInfo.java  (08-Oct-2012)
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


package com.cilogi.shiro.oauth.provider;

import com.cilogi.shiro.gae.UserAuthType;
import com.cilogi.shiro.oauth.OAuthInfo;

public interface IOAuthProviderInfo {
    /**
     * Which type of authorization is being used?  Each one is handled somewhat differently.
     * @return  The enum type
     */
    public UserAuthType getUserAuthType();

    /**
     * The URL to call on login, to get a code.
     * @param callbackUri  Where to go afterwards.
     * @return
     */
    public String loginURL(String callbackUri);
    /**
     * The URL to call on re-authentication, to get a code.
     * @param callbackUri  Where to go afterwards.
     * @return
     */
    public String reAuthenticateURL(String callbackUri);

    /**
     * Information about the user.  At the moment its pretty-much just the
     * email address.
     * @param code The auth token
     * @param callBackUrl Where to go afterwards
     * @return The info.
     */
    public OAuthInfo getUserInfo(String code, String callBackUrl);
    public void setState(String state);
}
