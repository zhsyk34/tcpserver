package com.dnk.smart.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptionalTest {

    @Test
    public void first() throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i + "");
        }
//        list = null;
        List<String> list2 = Optional.ofNullable(list).map(data -> data.stream().map(i -> i + "a").collect(Collectors.toList())).orElse(null);
        System.out.println(String.join(",", Optional.ofNullable(list2).orElse(new ArrayList<>())));

    }

    @Test
    public void stream() throws Exception {
        Stream<String> names = Stream.of("a", "b", "c");
        Optional<String> longest = names
                .filter(name -> name.startsWith("a"))
                .findFirst();

        Optional<String> r = longest.map(String::toUpperCase);
        System.out.println(r);

    }
}
