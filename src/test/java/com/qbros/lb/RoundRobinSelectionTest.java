package com.qbros.lb;

import com.qbros.lb.core.RoundRobinSelection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class RoundRobinSelectionTest {


    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 10, 15})
    void testIndexesSerially(int total) {

        RoundRobinSelection robinSelection = new RoundRobinSelection();

        for (long i = 1; i < total; i++) {
            System.out.println("next index is: " + robinSelection.pick(total));
        }

        Assertions.assertEquals(0, robinSelection.pick(total));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 10, 15})
    void testIndexesConcurrently(int total) throws InterruptedException {

        RoundRobinSelection robinSelection = new RoundRobinSelection();
        int nThreads = total;
        Executor executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(nThreads - 1);

        for (int i = 1; i < nThreads; i++) {
            executor.execute(() -> {
                robinSelection.pick(total);
                latch.countDown();
            });
        }

        latch.await();
        Assertions.assertEquals(0, robinSelection.pick(total));
    }
}
