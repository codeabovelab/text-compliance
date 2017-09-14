package com.codeabovelab.tpc.core.namedentity

import com.codeabovelab.tpc.core.nn.nlp.WordData
import com.codeabovelab.tpc.core.nn.nlp.isNullOrEmpty
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.core.processor.PredicateResult
import com.codeabovelab.tpc.core.processor.RulePredicate
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.*

/**
 */
@JsonTypeName("ThirdPartyNamesPredicate")
class ThirdPartyNamesPredicate(
        names: Collection<String>
) : RulePredicate<ThirdPartyNamesResult> {

    private val set: Set<String>

    init {
        val set = TreeSet<String>(String.CASE_INSENSITIVE_ORDER)
        set.addAll(names)
        this.set = Collections.unmodifiableSet(set)
    }

    override fun test(pc: PredicateContext, text: Text): ThirdPartyNamesResult {
        val entries = ArrayList<ThirdPartyNamesResult.Entry>()
        val si = pc.sentenceIterator(text)
        while(si.hasNext()) {
            val sd = si.next()
            if(sd.isNullOrEmpty()) {
                continue
            }
            for(wd in sd!!.words) {
                if(!wd.pos.isProperNoun) {
                    continue
                }
                if(isThirdParty(wd)) {
                    entries.add(ThirdPartyNamesResult.Entry(wd.str, text.getCoordinates(wd.offset, wd.str.length)))
                }
            }
        }
        return ThirdPartyNamesResult(entries = entries)
    }

    private fun isThirdParty(wd: WordData): Boolean {
        if(set.contains(wd.str)) {
            return false
        }
        if(wd.lemma != null && set.contains(wd.lemma)) {
            return false
        }
        return true
    }
}

class ThirdPartyNamesResult(
        entries: List<Entry>
) : PredicateResult<ThirdPartyNamesResult.Entry>(entries) {
    class Entry(
            val name: String,
            coordinates: TextCoordinates
    ) : PredicateResult.Entry(coordinates)
}