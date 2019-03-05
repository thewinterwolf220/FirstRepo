package com.phoenix;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import java.util.*;

class Utils {

    static void printFile(JavaRDD<String> file) {
        file.collect().forEach(System.out::println);
    }

    static void printItems(JavaRDD<String[]> list) {
        List<String[]> collect = list.collect();
        System.out.println("The list is made of " + collect.size() + " elements");
        collect.forEach(strings -> System.out.println(String.join(",", strings)));
    }

    static void printItemSupport(JavaPairRDD<String[], Double> itemSupport) {
        System.out.println("Items => Support");

        Map<String[], Double> asMap = itemSupport.collectAsMap();

        asMap.forEach((items, support) -> System.out.println(String.join(", ", items) + " => " + support));
    }

    /*
        Transforms a list of arrays made of just one string into a single array
        Helper for combinations.
     */
    static String[] listToArr(JavaRDD<String[]> list) {
        List<String[]> collect = list.collect();
        return collect.stream().flatMap(Arrays::stream).toArray(String[]::new);
    }

    /*
        Transforms an array of strings into just one string.
        The values are separated by a comma.
        The structure is thus flattened.
     */
    static String arrToString(String[] arr) {
        return String.join(", ", arr);
    }

    /*
        Takes a list of strings and formats them. In this case it would be the
        file parsed through JavaRDD.
    */
    static JavaRDD<String> sanitizeFile(JavaRDD<String> file) {
        // It's the same list, but the lines are made all equally formatted
        return file.map(String::toLowerCase).map(String::trim);
    }


    /*
         Checks the sanitised list, a file, and fetches all its different items.
         The return type is a list of arrays of strings. This choice is due to
         a better support from other methods that work on that type of structure.
         We must make sure that there are no duplicates.
        */
    static JavaRDD<String[]> fetchElements(JavaRDD<String> sanitisedList) {
        final String[] lines = sanitisedList.collect().toArray(new String[0]);

        Set<String> singleItems = new HashSet<>();

        Arrays.stream(lines)
                .map(line -> line.split(","))
                .map(Arrays::asList)
                .forEach(singleItems::addAll);

        return transformSingleListIntoRDD(singleItems);
    }

    private static JavaRDD<String[]> transformSingleListIntoRDD(Collection<String> singleItems) {
        // The structure is this: rows = number of items in singleItems
        // columns = 1 because there's gonna be just one element.
        List<String[]> list = new ArrayList<>();
        singleItems.forEach(s -> list.add(new String[]{s}));
        return Main.context.parallelize(list);
    }

    static void printRules(JavaRDD<Rule> rules) {
        rules.foreach(System.out::println);
    }
}
