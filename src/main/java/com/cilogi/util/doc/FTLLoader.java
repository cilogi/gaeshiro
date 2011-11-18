// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        FTLLoader.java
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

package com.cilogi.util.doc;

import com.google.common.base.Preconditions;
import freemarker.cache.URLTemplateLoader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

class FTLLoader extends URLTemplateLoader {
    static final Logger LOG = Logger.getLogger(FTLLoader.class.getName());

    private final URL baseURL;

    FTLLoader(URL baseURL) {
        Preconditions.checkNotNull(baseURL);
        this.baseURL = baseURL;
    }

    @Override
    public URL getURL(String templateName) {
        Preconditions.checkNotNull(templateName);
        try {
            URL url =  new URL(baseURL, templateName);
            URLConnection connect = url.openConnection();
            try {
                connect.connect();
                return url;
            } catch (IOException e) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
