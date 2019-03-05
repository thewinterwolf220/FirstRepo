package com.phoenix;

import org.apache.spark.api.java.JavaRDD;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

class Combinations {

    /*
        Returns the number of subsets that can be created given a certain set of items.
        It is just a summation of the binomial coefficient.
        Note that the argument, JavaRDD<String[]> refers to a set of strings containing
        only one item.
    */
    static long allPossibleSubSets(JavaRDD<String[]> items) {
        long n = items.count();
        return LongStream.rangeClosed(2, n)
                .map(k -> binomialCoefficient((int) n, (int) k))
                .sum();
    }

    /*
        Creates all the subsets from a given set of elements. Iterates from two to n.
    */
    static JavaRDD<String[]> generateAllCombinations(JavaRDD<String[]> items) {
        String[] singleItems = Utils.listToArr(items);

        int size = (int) allPossibleSubSets(items);
        List<String[]> all = new ArrayList<>(size);

        IntStream.rangeClosed(2, size)
                .mapToObj(i -> combinationsN(singleItems, i)).filter(Objects::nonNull)
                .forEach(all::addAll);

        return Main.context.parallelize(all);
    }

    /*
        Method chooser.
    */
    // FIXME: 15/01/19
    static List<String[]> combinationsN(String[] arr, int k) {
        List<String[]> combinations = new ArrayList<>();

        try {
            switch (k) {
                case 2:
                    combinations = combinations2(arr);
                    break;
                case 3:
                    combinations = combinations3(arr);
                    break;
                case 4:
                    combinations = combinations4(arr);
                    break;
                default:
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getClass().getCanonicalName());
            System.out.println("Yet to improve");
        }
        return combinations;
    }


    // TODO: 13/01/19  devise method that is more flexible to create combinations, try recursion.
    private static List<String[]> combinations4(String[] input) {
        List<String[]> list = new ArrayList<>();
        for (int a = 0; a < input.length - 3; a++)
            for (int i = 1; i < input.length - 2; i++)
                for (int j = i + 1; j < input.length - 1; j++)
                    for (int k = j + 1; k < input.length; k++)
                        list.add(new String[]{input[a], input[i], input[j], input[k]});
        return list;
    }

    private static List<String[]> combinations3(String[] input) {
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < input.length - 2; i++)
            for (int j = i + 1; j < input.length - 1; j++)
                for (int k = j + 1; k < input.length; k++)
                    list.add(new String[]{input[i], input[j], input[k]});
        return list;
    }

    private static List<String[]> combinations2(String[] input) {
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < input.length - 1; i++)
            for (int j = i + 1; j < input.length; j++)
                list.add(new String[]{input[i], input[j]});
        return list;
    }
}
