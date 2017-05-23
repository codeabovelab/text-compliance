package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.Text;
import com.codeabovelab.tpc.text.TextCoordinates;

import java.util.List;

/**
 * Test that rule match specified text
 */
public interface RulePredicate {
    /**
     * Test that specified text in context is match.
     * @param pc context
     * @param text text
     * @return coordinates when match or empty collection otherwise, newer return null
     */
    List<TextCoordinates> test(PredicateContext pc, Text text);
}
