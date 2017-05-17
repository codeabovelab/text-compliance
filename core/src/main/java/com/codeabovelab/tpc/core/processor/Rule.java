package com.codeabovelab.tpc.core.processor;

import lombok.Data;

/**
 */
@Data
public final class Rule implements Comparable<Rule> {

    private final String id;
    private final float weight;
    private final RulePredicate predicate;
    private final RuleAction action;


    @Override
    public int compareTo(Rule o) {
        return Float.compare(this.getWeight(), o.getWeight());
    }
}
