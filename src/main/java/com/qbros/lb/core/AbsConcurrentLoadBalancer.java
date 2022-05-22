package com.qbros.lb.core;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public abstract class AbsConcurrentLoadBalancer implements LoadBalancer {

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();

    /**
     * Write lock decorator
     *
     * @param runnable executable that should run mutually exclusive
     */
    protected void writeThreadSafe(Runnable runnable) {
        writeLock.lock();
        try {
            log.debug("Executing Thread [{}]", Thread.currentThread().getName());
            runnable.run();
        } finally {
            log.debug("Exiting Thread [{}]", Thread.currentThread().getName());
            writeLock.unlock();
        }
    }

    /**
     * Read lock decorator
     *
     * @param callable executable that should run mutually exclusive
     * @param <U>      Type of the return value
     * @return the result of read operation
     */
    protected <U> U readThreadSafe(Callable<U> callable) {
        readLock.lock();
        U result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return result;
    }
}

