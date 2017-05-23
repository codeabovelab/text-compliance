package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.text.TextCoordinates;
import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.List;

/**
 */
@Data
public final class RuleReport {
    private final String ruleId;
    private final List<TextCoordinates> coordinateses;

    public RuleReport(String ruleId, List<TextCoordinates> coordinateses) {
        this.ruleId = ruleId;
        this.coordinateses = ImmutableList.copyOf(coordinateses);
    }
}
