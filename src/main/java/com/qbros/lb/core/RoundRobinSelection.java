package com.qbros.lb.core;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoundRobinSelection implements SelectionStrategy {

    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    /**
     * Used the following NonBlocking RoundRobin solution
     * <a href="https://dzone.com/articles/atomicinteger-on-java-and-round-robin">link</a>
     *
     * @param total number of providers that we need to select one of them
     * @return the index of the selected provider
     */
    @Override
    public int pick(int total) {

        int counter;
        int nextIndex;

        do {
            counter = atomicCounter.get();
            nextIndex = (counter + 1) % total;
            log.debug("total {}, counter {}, next{}", total, counter, nextIndex);
        } while (!atomicCounter.compareAndSet(counter, nextIndex));

        return nextIndex;
    }

    @Override
    public String toString() {
        return "RoundRobinSelection";
    }
}
