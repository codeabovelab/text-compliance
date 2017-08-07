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

    private val compiled: Pattern = Pattern.compile(this.pattern)

    override fun test(pc: PredicateContext, text: Text): PredicateResult<*> {
        val matcher = compiled.matcher(text.data)
        var list: MutableList<PredicateResult.Entry>? = null
        while(matcher.find()) {
            val offset = matcher.start()
            val len = matcher.end() - offset
            val coord = text.getCoordinates(offset, len)
            if(list == null) {
                list = ArrayList()
            }
            list.add(PredicateResult.Entry(coord))
        }
        return PredicateResult(list.orEmpty())
    }
}
