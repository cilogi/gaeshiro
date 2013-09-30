// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        CreateDoc.java
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
import com.google.common.collect.Maps;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Create documents from FreeMarker templates. The documents can be text or images from SVG templates.
 * The locale and charset need to be defined, as well as a source of templates.
 */
public class CreateDoc implements ICreateDoc {
    private static final Logger LOG = Logger.getLogger(CreateDoc.class.getName());

    // used to share this common construct [just being fussy really]
    private static final byte[] ZERO_BYTES = new byte[0];

    /**
     * The root location where the templates are stored, either a local file,
     * or a directory served from a remote servlets server.
     */
    private final URL dataRoot;
    private final TemplateLoader templateLoader;

    private Configuration cfg;
    private final Locale locale;
    private final String charset;

    /**
     * Set up a new document creator.
     * @param dataRoot   The root URL of the templates used  to create documents.  This
     * does not have to <em>be</em> a template, but should be at the same directory level
     * as the template, or if not have a "/" at the end.   This can be a file URL if so required.
     * @param locale  The locale in which templates will be converted.  This typically affects things like the way
     * dates and numbers are printed.
     * @param charset  The character set to be used for documents. Its critically important to get this right.
     * Working on a windows computer usually results in some weird windows charset.  Better to stick with
     * <em>utf-8</em>  for normal stuff, and <em>utf-16</em> for Chinese and other non-alphabetic languages.  In windows
     * notepad allows for utf-8 or utf-16 (unicode).  Its important to edit files with an editor that obeys the rules.
     * @throws IOException  If FreeMarker can't be configured.
     */
    public CreateDoc(URL dataRoot, Locale locale, String charset) throws IOException {
        Preconditions.checkNotNull(dataRoot);
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(charset);
        Preconditions.checkNotNull(Charset.forName(charset));

        this.locale = locale;
        this.charset = charset.toLowerCase();
        this.dataRoot = dataRoot;
        this.templateLoader = null;
    }

    public CreateDoc(TemplateLoader templateLoader, Locale locale, String charset) throws IOException {
        Preconditions.checkNotNull(templateLoader);
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(charset);
        Preconditions.checkNotNull(Charset.forName(charset));

        this.locale = locale;
        this.charset = charset.toLowerCase();
        this.dataRoot = null;
        this.templateLoader = templateLoader;
    }

    /**
     * Get the right template for the locale.  Three locations are looked at
     * <el>
     * <it> The language + country of the locale (name_ll_cc), and if missing
     * <it> The language of the locale  (name_ll), and if missing
     * <it> The base file (name)
     * </el>
     * The files need extensions adding in the above of course.
     *
     * @param templateName      The name of template
     * @return A FreeMarker template
     * @throws IOException if the template can't be loaded
     */
    Template getTemplateForLocale(String templateName) throws IOException {
        String[] names = getFileNamesForSearch(locale, templateName);
        IOException lastException = null;
        for (String nm : names) {
            try {
                Template template = cfg().getTemplate(nm);
                template.setOutputEncoding(charset);
                return template;
            } catch (IOException e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            LOG.fine("problem getting template \"" + templateName + "\" for " + locale + " in dir " + dataRoot);
            throw lastException;
        }
        return null;
    }

    /**
     * Create a document from a template and a map
     * @param templateName  The  name of the template.
     * because we look for different files based on the locale.
     * @param map  The map passed in by FreeMarker when instantiating the template.
     * @return  The instantiated document.  Bytes are returned as this could be in any text encoding and will
     * often just be set as a servlets resource.  May be some mileage in returning a string, even though.
     * @throws IOException If there are any problems locating or processint the template.
     */
    public byte[] createDocument(String templateName, Map<String, ?> map) throws IOException {
        Template template = getTemplateForLocale(templateName);
        if (template == null) {
            return ZERO_BYTES;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(os, charset);
        try {
            template.process(map, out);
            out.close();
            return os.toByteArray();
        } catch (TemplateException e) {
            LOG.severe("Error processing template " + e.getMessage());
            return ZERO_BYTES;
        } finally {
            out.close();
        }
    }

    // to avoid having to create a map for short argument lists
    public static Map<String, Object> map(Object[] list) {
        Preconditions.checkNotNull(list);
        Preconditions.checkArgument(list.length % 2 == 0, "Your list has to have an even length, not " + list.length);

        Map<String,Object> out = Maps.newHashMap();
        for (int i = 0; i < list.length; i += 2) {
            out.put((String)list[i], list[i+1]);
        }
        return out;
    }


    private Configuration defaultConfiguration(URL baseURL) {
        Configuration config = new Configuration();
        config.setObjectWrapper(new DefaultObjectWrapper());
        try {
            config.setSetting(Configuration.CACHE_STORAGE_KEY, "strong:20, soft:250");
        } catch (TemplateException e) {
            LOG.warning("Can't set freemarker cache (not fatal) " + e.getMessage());
        }

        config.setDefaultEncoding(charset);
        config.setEncoding(locale, charset);
        config.setLocale(locale);
        TemplateLoader loader = (baseURL != null) ? new FTLLoader(baseURL) : templateLoader;
        config.setTemplateLoader(loader);
        return config;
    }

    private static String[] getFileNamesForSearch(Locale locale, String templateName) {
        String language = locale.getLanguage().toLowerCase();
        String country = locale.getCountry().toLowerCase();
        return new String[]{
                language + "_" + country + "_" + templateName,
                language + "_" + templateName,
                templateName
        };
    }

    public synchronized Configuration cfg() {
        if (cfg == null) {
            cfg = defaultConfiguration(dataRoot);
        }
        return cfg;
    }
}
