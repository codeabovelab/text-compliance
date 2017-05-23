package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.doc.Document;
import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Map;

/**
 * Read only context for predicate evaluation.
 */
@Data
public class PredicateContext {

    private final Document document;
    private final Map<String, Object> attributes;

    PredicateContext(ProcessingContext context) {
        this.document = context.getDocument();
        this.attributes = ImmutableMap.copyOf(context.getAttributes());
    }

    /**
     * Immutable map with document attributes.
     * @return non null immutable map
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
