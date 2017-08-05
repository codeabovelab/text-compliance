package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.util.SynchronizedDelegate
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.collect.ImmutableMap
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Report of document processing.
 * Must support conversion from JSON.
 */
class ProcessorReport(builder: Builder): Labeled {
    /**
     * Must be thread safe
     */
    class Builder {
        var documentId: String? by SynchronizedDelegate()
        var report: TextReport? by SynchronizedDelegate()
        val attributes: MutableMap<String, Any> = ConcurrentHashMap()

        fun build() = ProcessorReport(this)
    }

    val documentId: String
    val report: TextReport
    val attributes: Map<String, Any>
    @JsonIgnore
    override val labels: Collection<Label>

    init {
        this.documentId = builder.documentId ?: throw IllegalArgumentException("No documentId")
        this.report = builder.report!!
        this.attributes = ImmutableMap.copyOf(builder.attributes)
        this.labels = calcLabels()
    }

    private fun calcLabels(): Collection<Label> {
        val map = HashMap<String, MutableSimilarity>()
        visitLabels { label ->
            val sim = map.computeIfAbsent(label.label) { _ -> MutableSimilarity() }
            sim += label.similarity
        }
        val list = map.mapTo(ArrayList<Label>()) {
            e ->
            Label(e.key, e.value.value)
        }
        list.sortDescending()
        return list
    }

    fun visitLabels(visitor: (Label)-> Unit) {
        fun gather(report: TextReport) {
            for(rule in report.rules.values) {
                val res = rule.result
                if(res is Labeled) {
                    res.labels.forEach(visitor)
                } else {
                    for(entry in res.entries) {
                        if(entry is Labeled) {
                            entry.labels.forEach(visitor)
                        }
                    }
                }
            }
            report.childs.forEach { _, r ->  gather(r)}
        }
        gather(report)
    }

    fun visitEntries(visitor: (PredicateResult.Entry)-> Unit) {
        fun gather(report: TextReport) {
            for(rule in report.rules.values) {
                val res = rule.result
                for(entry in res.entries) {
                    visitor(entry)
                }
            }
            report.childs.forEach { _, r ->  gather(r)}
        }
        gather(report)
    }


    override fun toString(): String {
        return "ProcessorReport(documentId='$documentId', report=$report, attributes=$attributes)"
    }

    class TextReport(builder: Builder) {
        val rules: Map<String, RuleReport<*>> = ImmutableMap.copyOf(builder.rules)
        val textId: String = builder.textId!!
        val childs: Map<String, TextReport> = ImmutableMap.copyOf(builder.childs)

        class Builder {
            val rules: MutableMap<String, RuleReport<*>> = ConcurrentHashMap()
            var textId: String? by SynchronizedDelegate()
            val childs: MutableMap<String, TextReport> = ConcurrentHashMap()

            fun build() = TextReport(this)
        }

        override fun toString(): String {
            return "TextReport(rules=$rules, textId='$textId', childs=$childs)"
        }
    }
}
