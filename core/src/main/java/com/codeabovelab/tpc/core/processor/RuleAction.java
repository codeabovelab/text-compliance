package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.Text;

/**
 * Must be a thread safe.
 */
public interface RuleAction {
    void apply(Text text, ProcessingContext pc);
}
