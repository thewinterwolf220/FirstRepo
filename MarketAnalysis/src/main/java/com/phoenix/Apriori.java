package com.phoenix;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.phoenix.Main.context;

class Apriori {

    /*
     Counts how many times an element or a set of elements appears in the file
     The list must be already sanitised.
    */
    static long frequency(JavaRDD<String> sanitisedList, String... items) {
        return sanitisedList.collect().stream()
                .filter(line -> Arrays.stream(items).allMatch(item -> line.contains(item.toLowerCase())))
                .count();
    }


    /*
     Gets the support of an item set by computing how many times it appears 
     and dividing by the number of transactions, which is, of lines of the file.    
    */
    static double support(JavaRDD<String> allTransactions, String... elements) {
        long numOfTransactions = allTransactions.count();
        double appearances = frequency(allTransactions, elements);
        return appearances / numOfTransactions;
    }


    /*
        Given a collection of item sets and their original file, it creates a map.
        Here use a JavaPairRDD<String[], Double>.
    */
    static JavaPairRDD<String[], Double> itemSupport(JavaRDD<String[]> items, JavaRDD<String> transactions) {
        List<Tuple2<String[], Double>> pairs = new ArrayList<>();

        items.collect().forEach(item -> pairs.add(new Tuple2<>(item, support(transactions, item))));

        return context.parallelizePairs(pairs);
    }


    /*
        This method is fed a map, it checks if its support is higher than some expected value.
        It requires a prior knowledge of the support
        Returns a list of items that satisfy the condition.
    */
    static JavaRDD<String[]> filterSets(JavaPairRDD<String[], Double> itemSupport, double minSupp) {
        List<String[]> survivors = new ArrayList<>();

        itemSupport.filter(v1 -> v1._2 >= minSupp)
                .collectAsMap().forEach((set, supp) -> survivors.add(set));
        return context.parallelize(survivors);
    }


    /*
        This takes a list of sets and filters some out after computing their support.
        Thus the method does two things.
    */
    static JavaRDD<String[]> filterSets(JavaRDD<String> allTransactions, JavaRDD<String[]> itemsets, double minSupp) {

        List<String[]> survivors = itemsets.collect().stream()
                .filter(v1 -> support(allTransactions, v1) >= minSupp).collect(Collectors.toList());

        return context.parallelize(survivors);
    }

    /*
        Takes elements, create a Rule with it.
     */
    static JavaRDD<Rule> createRules(JavaRDD<String[]> singleItems) {

        List<Rule> rules = new ArrayList<>();
        List<String[]> singleItemsList = singleItems.collect();

        long count = singleItemsList.size();

        String[] premise, conclusion;
        for (int i = 0; i < count; i++) {
            premise = singleItemsList.get(i);
            for (int j = i + 1; j < count; j++) {

                conclusion = singleItemsList.get(j);
                Rule rule = new Rule(premise, conclusion);
                Rule opposite = new Rule(conclusion, premise);

                if (!rule.isIdentical()) {
                    rules.add(rule);
                    rules.add(opposite);
                }
            }
        }
        return context.parallelize(rules);
    }

//    static JavaRDD<Rule> create2Rules(JavaRDD<String[]> singleItems) {
//        List<Rule> rules = new ArrayList<>();
//
//        String[] singleitems = listToArr(singleItems);
//
//        List<String[]> conclusions = Combinations.combinationsN(singleitems, 2);
//
//        String[] premise, conclusion;
//        for (int i = 0; i < singleItems.size(); i++) {
//            premise = singleItems.get(i);
//            for (int j = 0; j < conclusions.size(); j++) {
//                conclusion = conclusions.get(j);
//                Rule rule = new Rule(premise, conclusion);
//                Rule opposite = new Rule(conclusion, premise);
//                if (!rule.isIdentical()) {
//                    rules.add(rule);
////                    rules.add();
//                }
//            }
//
//        }
//
//
//        return rules;
//    }

    static JavaRDD<Rule> filterRules(JavaRDD<Rule> rules, double minConfidence) {
        return rules.filter(rule -> rule.computeConfidence() >= minConfidence);
    }


}
