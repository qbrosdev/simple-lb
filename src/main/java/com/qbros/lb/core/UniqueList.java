package com.qbros.lb.core;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * We need a collection that provides:
 * 1- Random access to elements
 * 2- Unlike List it should not allow duplicates
 * <p>
 * The advantage is that the number of items is limited (10)
 * <p>
 * NOTE: This class is NOT thread safe!
 */
@Slf4j
public class UniqueList<T> {

    //is used to prevent duplicates
    private final Set<T> uniqueSet = new HashSet<>();
    //all the read and write operations are performed on this collection
    private final List<T> items = new ArrayList<>();
    private final int maxCapacity;

    public UniqueList() {
        this(10);
    }

    public UniqueList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Every modifying operation is mutually exclusive
     *
     * @param newItems list of items to add
     */
    public void addAll(List<T> newItems) {

        validateSize(newItems.size());

        for (T item : newItems) {
            if (!(uniqueSet.contains(item))) {
                uniqueSet.add(item);
                items.add(item);
            }
        }

        log.debug("Current items [{}] set by Thread [{}]", items, Thread.currentThread().getName());
    }

    /**
     * Every modifying operation is mutually exclusive
     *
     * @param newItem single item to add
     */
    public void addOne(T newItem) {

        if (!uniqueSet.contains(newItem)) {
            validateSize(1);
            uniqueSet.add(newItem);
            items.add(newItem);
            log.debug("items after add [{}]", items);
        } else {
            log.info("Element [{}] already exist in the collection, and it was nt added again", newItem);
        }
    }

    /**
     * Every modifying operation is mutually exclusive
     *
     * @param itemToRemove item to remove
     */
    public void remove(T itemToRemove) {

        if (uniqueSet.contains(itemToRemove)) {
            uniqueSet.remove(itemToRemove);
            items.remove(itemToRemove);
            log.debug("items after remove [{}]", items);
        } else {
            log.info("Element [{}] does not exist in the collection, and it can't be removed", itemToRemove);
        }
    }

    /**
     * Provides random access
     *
     * @param index index
     * @return the provider at index
     */
    public T getAtIndex(int index) {
        return (index < items.size()) ? items.get(index) : null;
    }

    /**
     * @return total number of elements in the collection
     */
    public int getSize() {
        return items.size();
    }

    public Collection<T> getContent() {
        return Collections.unmodifiableCollection(items);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ConcurrentUniqueArray{");
        sb.append("uniqueSet=").append(uniqueSet);
        sb.append(", Total number of items=").append(items.size());
        sb.append('}');
        return sb.toString();
    }

    private void validateSize(int addCount) {
        if (items.size() + addCount > maxCapacity) {
            throw new IllegalArgumentException("List exceeds the size");
        }
    }
}
