package com.phoenix;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import static com.phoenix.Main.file;
import static com.phoenix.Main.formattedFile;
import static com.phoenix.Utils.*;
import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void SanitizeFile() {
        JavaRDD<String> formattedFile = sanitizeFile(file);
        printFile(formattedFile);

        long lines = formattedFile.count();
        long expected = 4;
        assertEquals(expected, lines);
    }

    @Test
    public void FetchElements_PrintItems() {
        JavaRDD<String[]> javaRDD = fetchElements(formattedFile);

        long actual = javaRDD.count();
        long expected = 5;

        assertEquals(expected, actual);
        printItems(javaRDD);
    }

    @Test
    public void PrintItemSupport(){
        JavaPairRDD<String[], Double> itemSupport = Apriori.itemSupport(Main.elements, formattedFile);
        printItemSupport(itemSupport);
    }
}
