package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.Text;

/**
 */
public class SetAttributeAction implements RuleAction {
    private final String name;
    private final Object value;

    public SetAttributeAction(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void apply(Text text, ProcessingContext pc) {
        pc.getAttributes().put(name, value);
    }
}
