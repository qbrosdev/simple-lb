package com.qbros.lb;

import com.qbros.lb.core.UniqueList;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueListTest {

    @Test
    void addAll() {
        UniqueList<Integer> collection = new UniqueList<>();
        collection.addAll(List.of(1, 2, 3));
        collection.addAll(List.of(1, 2, 3, 4, 5, 6));
        assertThat(collection.getSize()).isEqualTo(6);
        assertThat(collection.getContent()).containsExactlyInAnyOrderElementsOf(List.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    void addOne() {
        UniqueList<Integer> collection = new UniqueList<>();
        collection.addOne(1);
        collection.addOne(2);
        collection.addOne(3);
        assertThat(collection.getSize()).isEqualTo(3);
        assertThat(collection.getContent()).containsExactlyInAnyOrderElementsOf(List.of(1, 2, 3));
    }

    @Test
    void addOne_with_duplicates() {
        UniqueList<Integer> collection = new UniqueList<>();
        collection.addOne(1);
        collection.addOne(2);
        collection.addOne(2);
        collection.addOne(3);
        collection.addOne(3);
        collection.addOne(3);
        assertThat(collection.getSize()).isEqualTo(3);
        assertThat(collection.getContent()).containsExactlyInAnyOrderElementsOf(List.of(1, 2, 3));
    }

    @Test
    void addOne_already_existing() {
        UniqueList<Integer> collection = new UniqueList<>();
        Set<Integer> uniqueSet = Stream.of(1, 2, 3).collect(Collectors.toSet());
        List<Integer> items = Stream.of(1, 2, 3).collect(Collectors.toList());
        ReflectionTestUtils.setField(collection, "uniqueSet", uniqueSet);
        ReflectionTestUtils.setField(collection, "items", items);

        collection.addOne(1);
        collection.addOne(2);
        collection.addOne(3);
        collection.addOne(3);
        collection.addOne(4);
        assertThat(collection.getSize()).isEqualTo(4);
        assertThat(collection.getContent()).containsExactlyInAnyOrderElementsOf(List.of(1, 2, 3, 4));
    }

    @Test
    void remove() {

        UniqueList<Integer> collection = new UniqueList<>();
        Set<Integer> uniqueSet = Stream.of(1, 2, 3).collect(Collectors.toSet());
        List<Integer> items = Stream.of(1, 2, 3).collect(Collectors.toList());
        ReflectionTestUtils.setField(collection, "uniqueSet", uniqueSet);
        ReflectionTestUtils.setField(collection, "items", items);
        collection.remove(2);
        assertThat(collection.getSize()).isEqualTo(2);
        assertThat(collection.getContent()).containsExactlyInAnyOrderElementsOf(List.of(1, 3));
    }

    @Test
    void remove_nonExisting() {

        UniqueList<Integer> collection = new UniqueList<>();
        collection.remove(4);
        assertThat(collection.getSize()).isEqualTo(0);
        assertThat(collection.getContent()).isEmpty();
    }

    @Test
    void getAtIndex() {
        UniqueList<Integer> collection = new UniqueList<>();
        Set<Integer> uniqueSet = Stream.of(1, 2, 3).collect(Collectors.toSet());
        List<Integer> items = Stream.of(1, 2, 3).collect(Collectors.toList());
        ReflectionTestUtils.setField(collection, "uniqueSet", uniqueSet);
        ReflectionTestUtils.setField(collection, "items", items);

        assertThat(collection.getAtIndex(1)).isEqualTo(2);
        assertThat(collection.getAtIndex(0)).isEqualTo(1);
        assertThat(collection.getAtIndex(2)).isEqualTo(3);
    }

    @Test
    void getAtIndex_outOfBound() {
        UniqueList<Integer> collection = new UniqueList<>();
        Set<Integer> uniqueSet = Stream.of(1, 2, 3).collect(Collectors.toSet());
        List<Integer> items = Stream.of(1, 2, 3).collect(Collectors.toList());
        ReflectionTestUtils.setField(collection, "uniqueSet", uniqueSet);
        ReflectionTestUtils.setField(collection, "items", items);

        assertThat(collection.getAtIndex(6)).isEqualTo(null);
    }

    @Test
    void getSize() {

        UniqueList<Integer> collection = new UniqueList<>();
        Set<Integer> uniqueSet = Stream.of(1, 2, 3).collect(Collectors.toSet());
        List<Integer> items = Stream.of(1, 2, 3).collect(Collectors.toList());
        ReflectionTestUtils.setField(collection, "uniqueSet", uniqueSet);
        ReflectionTestUtils.setField(collection, "items", items);

        assertThat(collection.getSize()).isEqualTo(3);
    }

    @Test
    void getSize_empty() {
        UniqueList<Integer> collection = new UniqueList<>();

        assertThat(collection.getSize()).isEqualTo(0);
    }

    @Test
    void getContent() {

        UniqueList<Integer> collection = new UniqueList<>();
        Set<Integer> uniqueSet = Stream.of(1, 2, 3).collect(Collectors.toSet());
        List<Integer> items = Stream.of(1, 2, 3).collect(Collectors.toList());
        ReflectionTestUtils.setField(collection, "uniqueSet", uniqueSet);
        ReflectionTestUtils.setField(collection, "items", items);

        assertThat(collection.getContent()).containsExactlyInAnyOrderElementsOf(List.of(1, 2, 3));
    }

    @Test
    void getContent_empty() {

        UniqueList<Integer> collection = new UniqueList<>();

        assertThat(collection.getContent()).isEmpty();
    }
}