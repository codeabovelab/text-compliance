package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.text.Text

import java.util.ArrayList
import java.util.regex.Pattern

/**
 */
class RegexPredicate(regexp: String): RulePredicate<PredicateResult<*>> {

    val pattern: Pattern = Pattern.compile(regexp)

    override fun test(pc: PredicateContext, text: Text): PredicateResult<*> {
        val matcher = pattern.matcher(text.data)
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
