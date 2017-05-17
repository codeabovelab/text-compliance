package com.codeabovelab.tpc.core.processor;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.Setter;
import lombok.Synchronized;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */
@Data
public final class ProcessorReport {

    /**
     * Must be thread safe
     */
    @Data
    public static final class Builder {
        @Setter(onMethod = @__(@Synchronized))
        private String documentId;
        private final List<RuleReport> rules = new CopyOnWriteArrayList<>();
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        public Builder documentId(String documentId) {
            setDocumentId(documentId);
            return this;
        }

        public ProcessorReport build() {
            return new ProcessorReport(this);
        }
    }

    private final String documentId;
    private final List<RuleReport> rules;

    public ProcessorReport(Builder b) {
        this.documentId = b.documentId;
        this.rules = ImmutableList.copyOf(b.rules);
    }

    public static Builder builder() {
        return new Builder();
    }
}
