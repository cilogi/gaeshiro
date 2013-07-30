// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        IGaeRegisteredUser.java  (30/07/13)
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


package com.cilogi.shiro.gaeuser;

import java.util.Date;

/**
 * A user who has been registered by us instead of being authorised by
 * a social media service.
 */
public interface IGaeRegisteredUser extends IGaeUser {
    public void register();
    public Date getDateRegistered();
    public String setRegistrationString(long hoursUntilExpiry);

    public void setPassword(String s);
    public String getPasswordHash();
    public byte[] getSalt();

    public boolean isSuspended();
    public void setSuspended(boolean b);

}