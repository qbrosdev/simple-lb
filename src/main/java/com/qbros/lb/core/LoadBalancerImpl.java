package com.qbros.lb.core;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LoadBalancerImpl extends AbsConcurrentLoadBalancer {

    private final UniqueList<Provider> providers;
    private final ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * It specifies how the next {@link  Provider} is selected.
     */
    private final SelectionStrategy selectionStrategy;
    /**
     * the state of the providers might change during the process of finding a provider and get value form it: {@link LoadBalancer#get()}
     * so we need to have some retry mechanism in place. and the {@code maxRetryCount} specifies the number of retries.
     */
    private final int maxRetryCount;
    private final int hbInitial;
    private final int hbNext;
    private final String name;

    @Builder
    private LoadBalancerImpl(SelectionStrategy selectionStrategy,
                             int maxCapacity,
                             int maxRetryCount,
                             int heartBeatCheckInitialDelay,
                             int heartBeatCheckNextDelay, String name) {

        this.providers = new UniqueList<>((maxCapacity != 0) ? maxCapacity : 10);
        this.selectionStrategy = (selectionStrategy != null) ? selectionStrategy : new RandomSelection();
        this.maxRetryCount = (maxRetryCount != 0) ? maxRetryCount : 3;
        this.hbInitial = (heartBeatCheckInitialDelay != 0) ? heartBeatCheckInitialDelay : 10;
        this.hbNext = (heartBeatCheckNextDelay != 0) ? heartBeatCheckNextDelay : 10;
        this.name = name;
        executorService.scheduleWithFixedDelay(this::checkProviders, hbInitial, hbNext, TimeUnit.SECONDS);
        log.info("Load Balancer initialized: [{}]", this);
    }

    @Override
    public String get() {

        int retryCount = 0;

        while (retryCount <= maxRetryCount) {

            AggregateProvidersStatus providersStatus = readThreadSafe(this::getAggregateProvidersStatus);

            if (providersStatus.availableCapacity == 0 || providersStatus.currentNumberOfProviders == 0) {
                log.warn("No providers available [{}]", providersStatus);
                throw new RuntimeException(String.format("No providers available: %s", providersStatus));
            }

            int nextServerIndex = selectionStrategy.pick(providersStatus.currentNumberOfProviders);

            Provider provider = readThreadSafe(() -> providers.getAtIndex(nextServerIndex));

            //could not find provider at the specified index maybe some other thread changed the providers list.
            if (provider == null) {
                continue;
            }

            if (provider.isAlive()) {
                return provider.provide();
            }

            retryCount++;
        }

        log.warn("No suitable provider was found after '{}' retries", maxRetryCount);
        return null;
    }

    @Override
    public void registerAll(List<Provider> newProviders) {
        writeThreadSafe(() -> providers.addAll(newProviders));
    }

    @Override
    public void include(Provider provider) {
        writeThreadSafe(() -> providers.addOne(provider));
    }

    @Override
    public void exclude(Provider provider) {
        writeThreadSafe(() -> providers.remove(provider));
    }

    @Override
    public void checkProviders() {
        try {
            Collection<Provider> items = providers.getContent();
            log.info("Heart Beat check by [{}]", name);
            log.debug("Heart Beat check by [{}] for providers [{}]", name, items);
            for (Provider item : items) {
                if (!item.isAlive()) {
                    providers.remove(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LoadBalancerImpl{");
        sb.append(", name=").append(name);
        sb.append(", providers=").append(providers);
        sb.append(", selection strategy=").append(selectionStrategy);
        sb.append(", maxRetryCount=").append(maxRetryCount);
        sb.append(", heat beat check Initial delay=").append(hbInitial);
        sb.append(", heat beat check next delay=").append(hbNext);
        sb.append('}');
        return sb.toString();
    }

    private AggregateProvidersStatus getAggregateProvidersStatus() {
        int totalRemainingCapacity = 0;
        for (Provider item : providers.getContent()) {
            totalRemainingCapacity += item.getConcurrentCapacity();
        }
        return new AggregateProvidersStatus(providers.getSize(), totalRemainingCapacity);
    }

    @Value
    class AggregateProvidersStatus {
        int currentNumberOfProviders;
        int availableCapacity;
    }

}
