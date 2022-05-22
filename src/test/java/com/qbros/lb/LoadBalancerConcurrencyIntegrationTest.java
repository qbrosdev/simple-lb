package com.qbros.lb;

import com.qbros.lb.core.LoadBalancer;
import com.qbros.lb.core.LoadBalancerImpl;
import com.qbros.lb.core.Provider;
import com.qbros.lb.core.UniqueList;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This long-running test slows the build process and can be categorised as Integration
 */
class LoadBalancerConcurrencyIntegrationTest {

    @RepeatedTest(10)
    void addAll() throws InterruptedException {

        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        LoadBalancer loadBalancer = LoadBalancerImpl.builder().maxCapacity(100).build();

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI1 = i;
            service.execute(() -> {
                List<Provider> list = IntStream.of(0, finalI1).mapToObj(it -> new Provider("P" + it)).collect(Collectors.toList());
                loadBalancer.registerAll(list);
                latch.countDown();
            });
        }

        latch.await();

        List<Provider> expectedContent = IntStream.range(0, numberOfThreads)
                .mapToObj(it -> new Provider("P" + it))
                .collect(Collectors.toList());

        UniqueList<Provider> providers = getProviders(loadBalancer);
        assert providers != null;
        assertThat(providers.getContent()).containsExactlyInAnyOrderElementsOf(expectedContent);
    }

    @RepeatedTest(10)
    void addOne() throws InterruptedException {

        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        LoadBalancer loadBalancer = LoadBalancerImpl.builder().maxCapacity(100).build();

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            service.execute(() -> {
                loadBalancer.include(new Provider("P" + finalI));
                latch.countDown();
            });
        }
        latch.await();
        UniqueList<Provider> providers = getProviders(loadBalancer);
        assert providers != null;
        assertThat(providers.getSize()).isEqualTo(100);
    }

    @RepeatedTest(10)
    void remove() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        LoadBalancer loadBalancer = LoadBalancerImpl.builder().maxCapacity(100).build();

        for (int i = 0; i < numberOfThreads; i++) {

            int finalI = i;

            service.execute(() -> {
                Provider provider = new Provider("p" + finalI);
                loadBalancer.include(provider);
                loadBalancer.exclude(provider);
                latch.countDown();
            });
        }

        latch.await();
        UniqueList<Provider> providers = getProviders(loadBalancer);
        assert providers != null;
        assertEquals(0, providers.getSize());
        assertThat(providers.getContent()).isEmpty();
    }

    @RepeatedTest(5)
    void getAtIndex_readerWillBeBlockedByWriter() throws InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(3);
        LoadBalancer loadBalancer = LoadBalancerImpl.builder().build();
        List<Provider> providers = List.of(new Provider("p" + 1), new Provider("p" + 2), new Provider("p" + 3));

        service.execute(() -> {
            loadBalancer.registerAll(providers);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(2000);
        UniqueList<Provider> actualProviders = getProviders(loadBalancer);
        String expected = actualProviders.getAtIndex(2).getId();
        assertThat(expected).isEqualTo("p3");
    }


    @SuppressWarnings("unchecked")
    private UniqueList<Provider> getProviders(LoadBalancer loadBalancer) {
        return (UniqueList<Provider>) ReflectionTestUtils.getField(loadBalancer, LoadBalancerImpl.class, "providers");
    }
}
