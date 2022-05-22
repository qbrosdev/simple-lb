package com.qbros.lb.core;

import java.util.List;

public interface LoadBalancer {

    /**
     * For simplicity, we will consider both the load
     * balancer and the provider having a public method
     * named get()
     *
     * @return the returned value from one of the providers
     */
    String get();

    /**
     * Step 2 – Register a list of providers
     *
     * @param providers Collection of servers that can be used by Load Balancer
     */
    void registerAll(List<Provider> providers);

    /**
     * Step 5 – Manual node exclusion
     *
     * @param provider The provider we want to re-include in the LoadBalancing
     */
    void include(Provider provider);

    /**
     * Step 5 – Manual node inclusion
     *
     * @param provider The provider we want to exclude in the LoadBalancing
     */
    void exclude(Provider provider);

    /**
     * Step 6 – Heart beat checker
     * The load balancer should invoke every X seconds
     * each of its registered providers on a special
     * method called check() to discover if they are alive
     * – if not, it should exclude the provider node from
     * load balancing.
     */
    void checkProviders();
}
