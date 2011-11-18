// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        TransactDAO.java  (01-Nov-2011)
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


package com.cilogi.shiro.gae;

import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.util.DAOBase;

import java.util.ConcurrentModificationException;
import java.util.logging.Logger;

/**
 * This is based on the recommended way of doing transactions in the Datastore
 * with objectify.
 */
public class TransactDAO extends DAOBase {
    static final Logger LOG = Logger.getLogger(TransactDAO.class.getName());

    public TransactDAO() {
        super(new ObjectifyOpts().setSessionCache(true).setBeginTransaction(true));
    }

    public static void runInTransaction(Transactable t) {
            TransactDAO dao = new TransactDAO();
            dao.doTransaction(t);
    }

    public void doTransaction(Transactable task) {
        try {
            task.run(this);
            ofy().getTxn().commit();
        } finally {
            if (ofy().getTxn().isActive()) {
                    ofy().getTxn().rollback();
            }
        }
    }

    public static void repeatInTransaction(Transactable t) {
        while (true) {
            try {
                runInTransaction(t);
                break;
            } catch (ConcurrentModificationException ex) {
                LOG.warning("Optimistic concurrency failure for " + t + ": " + ex);
            }
        }
    }

    public static <T> T runInTransaction(Transact<T> t) {
            TransactDAO dao = new TransactDAO();
            return dao.doTransaction(t);
    }

    public <T> T doTransaction(Transact<T> task) {
        try {
            task.run(this);
            ofy().getTxn().commit();
            return task.getResult();
        } finally {
            if (ofy().getTxn().isActive()) {
                    ofy().getTxn().rollback();
            }
        }
    }

    public static <T> T repeatInTransaction(Transact<T> t) {
        while (true) {
            try {
                runInTransaction(t);
                return t.getResult();
            } catch (ConcurrentModificationException ex) {
                LOG.warning("Optimistic concurrency failure for " + t + ": " + ex);
            }
        }
    }

    public static interface Transactable {
            public void run(TransactDAO dao);
    }

    public abstract static class Transact<T> implements Transactable {
            protected T result;
            public T getResult() {
                return this.result;
            }
    }
}
