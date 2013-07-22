// Copyright (c) 2013 Cilogi. All Rights Reserved.
//
// File:        java.java  (18/07/13)
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


package com.cilogi.util.gae.db;

import com.cilogi.util.ICounter;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A counter which can be incremented rapidly.
 *
 * Capable of incrementing the counter and increasing the number of shards. When
 * incrementing, a random shard is selected to prevent a single shard from being
 * written too frequently. If increments are being made too quickly, increase
 * the number of shards to divide the load. Performs datastore operations using
 * the low level datastore API.
 */
public class ShardedCounter implements ICounter {
    private static final Logger LOG = Logger.getLogger(ShardedCounter.class.getName());

    private static final class Counter {
        private static final String KIND = "Counter";
        private static final String SHARD_COUNT = "shard_count";
    }

    private static final class CounterShard {
        private static final String KIND_PREFIX = "CounterShard_";
        private static final String COUNT = "count";
    }

    private static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService();

    private static final int INITIAL_SHARDS = 5;

    private static final int CACHE_PERIOD_SECONDS = 60;

    private final String counterName;
    private final Random generator = new Random();
    private String kind;
    private final MemcacheService mc = MemcacheServiceFactory.getMemcacheService();

    /**
     * Constructor which creates a sharded counter using the provided counter
     * name.
     *
     * @param name name of the sharded counter
     */
    public ShardedCounter(final String name) {
        counterName = name;
        kind = CounterShard.KIND_PREFIX + counterName;
    }

    /**
     * Increase the number of shards for a given sharded counter. Will never
     * decrease the number of shards.
     *
     * @param count Number of new shards to build and store
     */
    public final void addShards(final int count) {
        Key counterKey = KeyFactory.createKey(Counter.KIND, counterName);
        incrementPropertyTx(counterKey, Counter.SHARD_COUNT, count,
                INITIAL_SHARDS + count);
    }

    /**
     * Retrieve the value of this sharded counter.
     *
     * @return Summed total of all shards' counts
     */
    @Override
    public final long getCount() {
        Long value = (Long) mc.get(kind);
        if (value != null) {
            return value;
        }

        long sum = 0;
        Query query = new Query(kind);
        for (Entity shard : DS.prepare(query).asIterable()) {
            sum += (Long) shard.getProperty(CounterShard.COUNT);
        }
        mc.put(kind, sum, Expiration.byDeltaSeconds(CACHE_PERIOD_SECONDS),
                SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        return sum;
    }

    /**
     * Increment the value of this sharded counter.
     */
    @Override
    public final void increment() {
        delta(1L);
    }

    @Override
    public void decrement() {
        delta(-1L);
    }

    private void delta(long delta) {
        int numShards = getShardCount();

        long shardNum = generator.nextInt(numShards);

        Key shardKey = KeyFactory.createKey(kind, Long.toString(shardNum));
        incrementPropertyTx(shardKey, CounterShard.COUNT, delta, delta);
        mc.increment(kind, delta);
    }

    private int getShardCount() {
        try {
            Key counterKey = KeyFactory.createKey(Counter.KIND, counterName);
            Entity counter = DS.get(counterKey);
            Long shardCount = (Long) counter.getProperty(Counter.SHARD_COUNT);
            return shardCount.intValue();
        } catch (EntityNotFoundException ignore) {
            return INITIAL_SHARDS;
        }
    }

    /**
     * Increment datastore property value inside a transaction. If the entity
     * with the provided key does not exist, instead create an entity with the
     * supplied initial property value.
     *
     * @param key the entity key to update or create
     * @param prop the property name to be incremented
     * @param increment the amount by which to increment
     * @param initialValue the value to use if the entity does not exist
     */
    private void incrementPropertyTx(final Key key, final String prop, final long increment, final long initialValue) {
        Transaction tx = DS.beginTransaction();
        Entity thing;
        long value;
        try {
            try {
                thing = DS.get(tx, key);
                value = (Long) thing.getProperty(prop) + increment;
            } catch (EntityNotFoundException e) {
                thing = new Entity(key);
                value = initialValue;
            }
            thing.setUnindexedProperty(prop, value);
            DS.put(tx, thing);
            tx.commit();
        } catch (ConcurrentModificationException e) {
            LOG.log(Level.WARNING, "You may need more shards. Consider adding more shards.");
            LOG.log(Level.WARNING, e.toString(), e);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.toString(), e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
}
