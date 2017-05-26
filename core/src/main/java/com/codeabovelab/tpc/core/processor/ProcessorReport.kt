package com.codeabovelab.tpc.core.processor;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.Setter;
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


/**
 */
class ProcessorReport(builder: Builder) {
    /**
     * Must be thread safe
     */
    class Builder {
        var documentId: String? = null
            @Synchronized get() = field
            @Synchronized set(value) {
                field = value
            }

        val rules = CopyOnWriteArrayList<RuleReport>()
        val attributes = ConcurrentHashMap<String, Any>()

        fun build() = ProcessorReport(this)
    }

    val documentId: String
    val rules: List<RuleReport>
    val attributes: Map<String, Any>

    init {
        this.documentId = builder.documentId ?: throw IllegalArgumentException("No documentId")
        this.rules = builder.rules
        this.attributes = builder.attributes
    }

    override fun toString(): String {
        return "ProcessorReport(documentId='$documentId', rules=$rules, attributes=$attributes)"
    }
}
