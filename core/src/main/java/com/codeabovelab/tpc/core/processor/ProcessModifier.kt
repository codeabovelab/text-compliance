package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.Textual

/**
 * Provide handlers and options for modify document processing.
 * All arguments of this class must have a default parameters.
 */
class ProcessModifier(
        /**
         * Filter which text must be processed (true) or not (false). For example it allow exclude document fields.
         * @return false when specified text must be processed
         */
        val filter: (Textual) -> Boolean = { true }
) {
    companion object {
        val DEFAULT = ProcessModifier()
    }
}