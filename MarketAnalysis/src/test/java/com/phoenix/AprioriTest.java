package com.phoenix;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import java.util.Map;

import static com.phoenix.Apriori.*;
import static com.phoenix.Main.elements;
import static com.phoenix.Main.formattedFile;
import static org.junit.Assert.assertEquals;

public class AprioriTest {


    @Test
    public void Frequency() {
        String[] items = {"papaya", "milk"};

        long expected = 1;
        long actual = frequency(formattedFile, items);

        assertEquals(expected, actual);
        System.out.println("Success");
    }

    @Test
    public void FrequencyWithNonExistent() {
        String[] items = {"papaya", "milk", "beer"};

        long expected = 0;
        long actual = frequency(formattedFile, items);

        assertEquals(expected, actual);
        System.out.println("Success");
    }

    @Test
    public void Support() {
        String[] item = {"papaya"};
        double expected = 0.25;
        double actual = support(formattedFile, item);

        assertEquals(expected, actual, 0.00001);
        System.out.println("Success");
    }


    @Test
    public void ItemSupport() {
        JavaPairRDD<String[], Double> itemSupport = itemSupport(elements, formattedFile);

        Map<String[], Double> map = itemSupport.collectAsMap();

        map.forEach((items, support) -> System.out.println(String.join(", ", items) + " => " + support));
    }

    @Test
    public void FilterItemSupport() {
        JavaPairRDD<String[], Double> itemSupport = itemSupport(elements, formattedFile);

        JavaRDD<String[]> sets = filterSets(itemSupport, 0.5);
        Utils.printItems(sets);

        int actual = sets.collect().size();
        assertEquals(4, actual);
    }
//
//    @Test
//    public void Filter_TwoElementsSets() {
//    }
//
//    @Test
//    public void ItemSupport_OneElement_andFilter() {
//    }
//
//    @Test
//    public void ItemSupportTwoElementsAndFilter() {
//
//    }
//
//    @Test
//    public void GenerateAllSubsets() {
//    }
//

    @Test
    public void Confidence_one_to_two() {
    }
}
