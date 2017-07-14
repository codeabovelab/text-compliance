package com.codeabovelab.tpc.core.processor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.cast


/**
 * Report of document processing.
 * Must support conversion from JSON.
 */
class ProcessorReport(builder: Builder): Labeled {
    /**
     * Must be thread safe
     */
    class Builder {
        var documentId: String? = null
            @Synchronized get() = field
            @Synchronized set(value) {
                field = value
            }

        val rules = CopyOnWriteArrayList<RuleReport<*>>()
        val attributes = ConcurrentHashMap<String, Any>()

        fun build() = ProcessorReport(this)
    }

    val documentId: String
    val rules: List<RuleReport<*>>
    val attributes: Map<String, Any>
    override val labels: Collection<Label>

    init {
        this.documentId = builder.documentId ?: throw IllegalArgumentException("No documentId")
        this.rules = builder.rules
        this.attributes = builder.attributes
        this.labels = calcLabels()
    }

    private fun calcLabels(): Collection<Label> {
        val map = HashMap<String, MutableSimilarity>()
        fun aggregate(label: Label) {
            val sim = map.computeIfAbsent(label.label) { _ -> MutableSimilarity() }
            sim += label.similarity
        }
        for(rule in this.rules) {
            val res = rule.result
            if(res is Labeled) {
                res.labels.forEach(::aggregate)
            } else {
              for(entry in res.entries) {
                  if(entry is Labeled) {
                      entry.labels.forEach(::aggregate)
                  }
              }
            }
        }
        return map.mapTo(ArrayList<Label>()) {
            e -> Label(e.key, e.value.value)
        }
    }

    override fun toString(): String {
        return "ProcessorReport(documentId='$documentId', rules=$rules, attributes=$attributes)"
    }

    inline fun <reified T : PredicateResult<*>> findPredicateResult(): T? {
        val type = T::class
        val res = rules.find {
            type.isInstance(it.result)
        }
        return if(res != null) type.cast(res.result) else null
    }
}
