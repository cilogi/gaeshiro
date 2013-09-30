// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        IRenderView.java  (30/09/13)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Cilogi (the Author) and may not be used, sold, licenced, 
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


package com.cilogi.web.servlets.shiro.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface IRenderView {

    public void defaultRender(HttpServletRequest request,
                              HttpServletResponse response,
                              String name) throws IOException;

    public void render(HttpServletRequest request,
                       HttpServletResponse response,
                       String name,
                       Map<String,Object> bindings) throws IOException;
}