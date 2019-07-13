package hello;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class HelloWorld {
    private List<String> list;

    public static void main(String[] args) {
        //cartesian();
        //countElements();
        listOfMapsToMapOfLists();
    }

    public static <T> Predicate<T> distinct(long atLeast) {
        Map<T, Long> map = new ConcurrentHashMap<>();
        return t -> map.merge(t, 1L, Long::sum) == atLeast;
    }

    public static void countElements() {
        List<String> list = Arrays.asList("foo", "foo", "bar", "baz", "baz", "baz");

        // 1
        Map<String, Long> counts = list.stream().collect(groupingBy(x -> x, counting()));
        counts.values().removeIf(cnt -> cnt < 3);
        counts.keySet().forEach(System.out::println);

        // 2
        list.stream().filter(distinct(3)).forEach(System.out::println);
    }

    public static void cartesian()
    {
        List<String> a1 = asList("a", "b", "c");
        List<String> a2 = asList("x", "y");
        List<String> a3 = asList("1", "2", "3");

        //a1.stream().flatMap(x -> a2.stream().flatMap(y -> a3.stream().map(z -> x + y + z))).forEach(System.out::println);

        List<List<String>> as = asList(a1, a2, a3);

        Supplier<Stream<String>> s = as.stream()
                .<Supplier<Stream<String>>>map(list -> list::stream)
                .reduce(
                        (sup1, sup2) ->
                                () -> sup1.get().flatMap(e1 -> sup2.get().map(e2 -> e1 + e2))
                )
                .orElse(() -> Stream.of(""));

        s.get().forEach(System.out::println);
    }

    public static void listOfMapsToMapOfLists() {
        List<Map<String, Integer>> input = Arrays.asList(new HashMap<>() {
            {
                put("a", 3);
                put("b", 4);
            }
        }, new HashMap<>() {
            {
                put("a", 5);
                put("b", 7);
            }
        }, new HashMap<>() {
            {
                put("a", 53);
                put("b", 72);
            }
        });

        Map<String, List<Integer>> m = input.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(groupingBy(
                        Map.Entry::getKey,
                        mapping(Map.Entry::getValue, toList())));

        System.out.println(m);

        Map<String, List<Integer>> mm = input.stream()
                .flatMap(map -> map.entrySet().stream())
                .reduce(new ConcurrentHashMap<>(), (acc, x) -> {
                    List<Integer> val = acc.get(x.getKey());
                    if (val == null) {
                        List<Integer> l = new ArrayList<>();
                        l.add(x.getValue());
                        acc.put(x.getKey(), l);
                    } else
                        val.add(x.getValue());
                    return acc;
                }, (m1, m2) -> m1);

        System.out.println(mm);
    }
}
