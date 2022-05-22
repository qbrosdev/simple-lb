package com.qbros.lb.core;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class RandomSelection implements SelectionStrategy {

    @Override
    public int pick(int total) {
        return ThreadLocalRandom.current().nextInt(total);
    }

    @Override
    public String toString() {
        return "RandomSelection";
    }
}