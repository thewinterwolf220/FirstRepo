package com.phoenix;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import static com.phoenix.Combinations.allPossibleSubSets;
import static com.phoenix.Combinations.generateAllCombinations;
import static com.phoenix.Main.elements;
import static com.phoenix.Main.formattedFile;
import static org.junit.Assert.assertEquals;

public class CombinationsTest {

    @Test
    public void AllPossibleSubsets() {
        long actual = allPossibleSubSets(elements);
        assertEquals(26, actual);
    }

    @Test
    public void GenerateAllSubsets() {
        JavaPairRDD<String[], Double> itemSupport = Apriori.itemSupport(elements, formattedFile);
        JavaRDD<String[]> combinations = generateAllCombinations(Apriori.filterSets(itemSupport, 0.5));
        Utils.printItems(combinations);
        assertEquals(11, combinations.collect().size());
    }


}
