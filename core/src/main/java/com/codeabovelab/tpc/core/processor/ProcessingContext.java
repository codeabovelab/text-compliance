package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.text.Text;
import com.codeabovelab.tpc.text.TextCoordinates;
import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Processing context, it can be used in concurrent environment.
 * Must be thread save.
 */
@Data
public final class ProcessingContext {
    private final ProcessorReport.Builder reportBuilder;
    private final Document document;
    private final List<Rule> rules;

    ProcessingContext(Document document, ProcessorReport.Builder reportBuilder, List<Rule> rules) {
        this.document = document;
        this.reportBuilder = reportBuilder;
        this.rules = rules;
    }

    void onText(Text text) {
        rules.forEach(r -> applyRule(text, r));
    }

    private void applyRule(Text text, Rule rule) {
        RulePredicate predicate = rule.getPredicate();
        PredicateResult coords = predicate.test(getPredicateContext(), text);
        if(coords.isEmpty()) {
           return;
        }
        getReportBuilder().getRules().add(new RuleReport(rule.getId(), coords));
        rule.getAction().apply(text, this);
    }

    private PredicateContext getPredicateContext() {
        // now predicate context has snapshot of attributes
        // therefore we can not share it instance
        return new PredicateContext(this.getDocument(), ImmutableMap.copyOf(getAttributes()));
    }

    /**
     * Mutable map of attributes.
     * @return map of attributes
     */
    public Map<String, Object> getAttributes() {
        return this.getReportBuilder().getAttributes();
    }
}
