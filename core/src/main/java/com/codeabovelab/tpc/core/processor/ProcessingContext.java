package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.text.Text;
import com.codeabovelab.tpc.text.TextCoordinates;
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
        //TODO we must pass some date to action (for example when predicate produce percentage or something else)
        // but we can use trick: 'true predicate' and embed predicate into action
        List<TextCoordinates> coords = predicate.test(getPredicateContext(), text);
        if(coords.isEmpty()) {
           return;
        }
        getReportBuilder().getRules().add(new RuleReport(rule.getId(), coords));
        rule.getAction().apply(text, this);
    }

    private PredicateContext getPredicateContext() {
        // now predicate context has snapshot of attributes
        // therefore we can not share it instance
        return new PredicateContext(this);
    }

    /**
     * Mutable map of attributes.
     * @return map of attributes
     */
    public Map<String, Object> getAttributes() {
        return this.getReportBuilder().getAttributes();
    }
}
