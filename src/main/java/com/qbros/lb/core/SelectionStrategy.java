package com.qbros.lb.core;

/**
 * This interface defines contract for different selection strategies.
 */
public interface SelectionStrategy {

    /**
     * picks a random number which specifies the index of the next selection item
     *
     * @param total all possible options
     * @return the picked number from all possible options
     */
    int pick(int total);
}
