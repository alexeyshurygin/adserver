package com.alexeyshurygin;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexey Shurygin
 */
public class BannerStats {
    private static final BannerStats instance = new BannerStats();

    private final ConcurrentMap<String, SortedSet<Long>> stats = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    private BannerStats() {
    }

    public static BannerStats getInstance() {
        return instance;
    }

    public void addTime(String id) {
        final long time = System.currentTimeMillis();
        ReadWriteLock lock = locks.get(id);
        if (lock == null) {
            lock = new ReentrantReadWriteLock();
            final ReadWriteLock lock1 = locks.putIfAbsent(id, lock);
            if (lock1 != null) {
                lock = lock1;
            }
        }
        final Lock w = lock.writeLock();
        w.lock();
        try {
            SortedSet<Long> times = stats.get(id);
            if (times == null) {
                times = new TreeSet<>();
                stats.put(id, times);
            }

            times.add(time);
        } finally {
            w.unlock();
        }
    }
}
