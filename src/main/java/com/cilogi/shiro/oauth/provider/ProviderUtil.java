// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        ProviderUtil.java  (16-Oct-2012)
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


class ProviderUtil {
    static final Logger LOG = Logger.getLogger(ProviderUtil.class.getName());

    private ProviderUtil() {

    }

    public static String makeRoot(String fullURL, String redirect) {
        try {
            URL url = new URL(fullURL);
            String portString = (url.getPort() == -1) ? "" : ":" + url.getPort();
            String absRedirect = redirect.startsWith("/") ? redirect : "/" + redirect;
            return url.getProtocol() + "://" + url.getHost() + portString + absRedirect;
        } catch (MalformedURLException e) {
            return fullURL;
        }
    }    
}
