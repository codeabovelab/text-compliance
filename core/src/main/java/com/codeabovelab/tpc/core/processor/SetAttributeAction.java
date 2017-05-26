package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.Text;

/**
 */
public class SetAttributeAction implements RuleAction<PredicateResult<?>> {
    private final String name;
    private final Object value;

    public SetAttributeAction(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void apply(ProcessingContext pc, Text text, PredicateResult<?> result) {
        pc.getAttributes().put(name, value);
    }
}
