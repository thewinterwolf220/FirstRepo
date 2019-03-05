package com.phoenix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RuleTest {

    @Test
    public void ImpliesItself() {
        String[] milk = {"milk"};
        Rule rule = new Rule(milk, milk);
        assertEquals(1.0, rule.computeConfidence(), 0.001);
    }

    @Test
    public void Confidence_one_to_one() {
        String[] premise = {"apples"};
        String[] conclusion = {"diapers"};

        Rule rule = new Rule(premise, conclusion);
        assertEquals(1.0, rule.computeConfidence(), 0.0001);
    }

}
