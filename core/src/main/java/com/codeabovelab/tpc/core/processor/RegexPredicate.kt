package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.Text
import com.fasterxml.jackson.annotation.JsonTypeName

import java.util.ArrayList
import java.util.regex.Pattern

/**
 */
@JsonTypeName("RegexPredicate")
class RegexPredicate(
        /**
         * Need as property for json representation
         */
        val pattern: String
): RulePredicate<PredicateResult<*>> {

    private val compiled: Pattern = Pattern.compile(this.pattern, Pattern.MULTILINE or Pattern.DOTALL)

    override fun test(pc: PredicateContext, text: Text): PredicateResult<*> {
        val matcher = compiled.matcher(text.data)
        var list: MutableList<PredicateResult.Entry>? = null
        while(matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            if(start == end) {
                // it happens on some patterns
                continue
            }
            val len = end - start
            val coord = text.getCoordinates(start, len)
            if(list == null) {
                list = ArrayList()
            }
            list.add(PredicateResult.Entry(coord))
        }
        return PredicateResult(list.orEmpty())
    }
}
