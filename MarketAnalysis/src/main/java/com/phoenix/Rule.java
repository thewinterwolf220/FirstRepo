package com.phoenix;

import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.phoenix.Apriori.support;

/*
    A rule has a premise and a conclusion.
    A premise implies a conclusion. p => q.

    An example is {milk} => {diapers}

    Of each rule we compute the confidence. The confidence is given by the formula :

             supp(premise U conclusion)     2/4
        C = ---------------------------- = ------ = 2/3 = 0.67
                  supp(premise)             3/4

    In the above case, the rule comprises single items. However, they're meant
    to include sets of elements, so both the premise and the conclusion ought
    to be arrays of strings.

    The confidence level is a value between 1 and 0.

 */


class Rule implements Comparable {

    private Predicate premise;
    private Predicate conclusion;

    /*
        Consider instantiating a double variable confidence that stores the computed value.
        Works as a sort of cache.
     */


    Rule(String[] premise, String[] conclusion) {
        this.premise = new Predicate(premise);
        this.conclusion = new Predicate(conclusion);
    }


    /*
        Checks if the premise and the conclusion are equals, in that case
        the confidence would always be equal to 1.
     */
    boolean isIdentical() {
        return Utils.arrToString(premise.elements).equals(Utils.arrToString(conclusion.elements));
    }

    /*
        In order to compute the confidence we need to know the support of the union of the elements
        We can create a tmp array that holds the union of those elements and compute their support.
    */
    private String[] union() {
        int size = premise.elements.length + conclusion.elements.length;

        List<String> union = new ArrayList<>(size);
        union.addAll(Arrays.asList(premise.elements));
        union.addAll(Arrays.asList(conclusion.elements));

        return union.toArray(new String[size]);
    }

    double computeConfidence() {
        return support(Main.formattedFile, union()) / support(Main.formattedFile, premise.elements);
    }

    @Override
    public String toString() {
        return Utils.arrToString(premise.elements) + " =>  "
                + Utils.arrToString(conclusion.elements) + " = " + computeConfidence();

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (!(o instanceof Rule)) System.exit(1);
        Rule another = (Rule) o;
        return Double.compare(computeConfidence(), another.computeConfidence());
    }

    class Predicate {
        String[] elements;

        Predicate(String[] elements) {
            this.elements = elements;
        }
    }
}
