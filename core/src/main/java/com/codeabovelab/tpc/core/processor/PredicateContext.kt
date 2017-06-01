package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.doc.Document

/**
 * Read only context for predicate evaluation.
 */
data class PredicateContext(val document: Document, val attributes: Map<String, Any> )
