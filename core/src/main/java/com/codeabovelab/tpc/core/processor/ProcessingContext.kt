package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorFactory
import com.codeabovelab.tpc.core.thread.MessagesThread
import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.Textual
import com.google.common.collect.ImmutableMap
import java.util.*

/**
 * Processing context, it can be used in concurrent environment.
 * Must be thread save.
 */
class ProcessingContext(
        val document: Document,
        val modifier: ProcessModifier,
        val rules: List<Rule<*>>,
        val thread: MessagesThread,
        private val sentenceIteratorFactory: SentenceIteratorFactory
) {

    val reportBuilder = ProcessorReport.Builder()
    private val textualBuilders = IdentityHashMap<Textual, ProcessorReport.TextReport.Builder>()

    fun build(): ProcessorReport {
        reportBuilder.documentId = document.id
        fun buildTextReport(textual: Textual): ProcessorReport.TextReport? {
            val builder = textualBuilders[textual]
            if(builder == null) {
                return null
            }
            for(child in textual.childs) {
                val report = buildTextReport(child)
                if(report != null) {
                    builder.childs[child.id] = report
                }
            }
            return builder.build()
        }
        reportBuilder.report = buildTextReport(document)!!
        return reportBuilder.build()
    }

    fun onText(textual: Textual, text: Text) {
        if(!modifier.filter(textual)) {
            return
        }
        val handledText = modifier.textHandler(text)
        val ruleCtx = RuleContext(processingContext = this, text = handledText)
        ruleCtx.textReport.textId = textual.id
        rules.forEach {
            ruleCtx.handleRule(it)
        }
        textualBuilders.put(textual, ruleCtx.textReport)
    }

    internal fun getPredicateContext(): PredicateContext {
        // now predicate context has snapshot of attributes
        // therefore we can not share it instance
        return PredicateContext(
                document = document,
                attributes = ImmutableMap.copyOf(attributes),
                thread = thread,
                sentenceIteratorFactory = sentenceIteratorFactory
        )
    }

    /**
     * Mutable map of attributes.
     * @return map of attributes
     */
    val attributes : MutableMap<String, Any> = reportBuilder.attributes
}
