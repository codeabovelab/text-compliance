package com.codeabovelab.tpc.core.processor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.cast


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

        val rules = CopyOnWriteArrayList<RuleReport<*>>()
        val attributes = ConcurrentHashMap<String, Any>()

        fun build() = ProcessorReport(this)
    }

    val documentId: String
    val rules: List<RuleReport<*>>
    val attributes: Map<String, Any>

    init {
        this.documentId = builder.documentId ?: throw IllegalArgumentException("No documentId")
        this.rules = builder.rules
        this.attributes = builder.attributes
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
