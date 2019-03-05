package com.phoenix;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;

import static com.phoenix.Apriori.*;
import static com.phoenix.Utils.*;

public class Main {
    static SparkConf conf = new SparkConf().setMaster("local").setAppName("Market Analysis");
    static JavaSparkContext context = new JavaSparkContext(conf);
    static JavaRDD<String> file = context.textFile("data/retail_small.txt");
    static JavaRDD<String> formattedFile = sanitizeFile(file);

     static JavaRDD<String[]> elements = fetchElements(formattedFile);

    public static void main(String[] args) {

        double minSupp = 0.5;
        double minConf = 0.6;

        /*
        Sanitise list.
        Extract all the items. You will need an array.
        Filter the low-frequency ones.
        Create all the subsets with the remaining ones.
        Compute support for each one.

        Make associations and determine confidence.
        */


    }

}
