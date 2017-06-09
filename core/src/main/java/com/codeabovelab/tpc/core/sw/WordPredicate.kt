package com.codeabovelab.tpc.core.sw

import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.core.processor.PredicateResult
import com.codeabovelab.tpc.core.processor.RulePredicate
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import org.deeplearning4j.text.uima.UimaResource

/**
 */
class WordPredicate(
        val keywordMatcher: KeywordMatcher,
        val uima: UimaResource,
        val wordSupplier: (wc: WordContext) -> String?
): RulePredicate<WordSearchResult> {

    override fun test(pc: PredicateContext, text: Text): WordSearchResult {
        val si = SentenceIteratorImpl.create(uima, TextIterator.singleton(text))
        val entries = ArrayList<WordSearchResult.Entry>()
        while(si.hasNext()) {
            val sentence = si.next()
            if(sentence.isNullOrEmpty()) {
                continue
            }
            val seq = sentence!!
            val vws = toWordList(seq)
            if(vws.isEmpty()) {
                return WordSearchResult(entries = emptyList())
            }
            val resEntry = WordSearchResult.Entry(
                    coordinates = text.getCoordinates(seq.offset, seq.str.length),
                    word = vws)
            entries.add(resEntry)
        }
        return WordSearchResult(entries = entries)
    }

    private fun toWordList(seq: SentenceData): List<WordSearchResult.Label> {
        val wch = WordContext.create()
        wch.sentence = seq
        val vws = ArrayList<WordSearchResult.Label>()
        for (word in seq.words) {
            wch.word = word
            val str = wordSupplier(wch.context)
            if (str.isNullOrEmpty()) {
                continue
            }
            if (!keywordMatcher.test(wch.context)) {
                continue
            }
            vws.add(WordSearchResult.Label(str!!, word.lemma!!))
        }
        return vws
    }
}

class WordSearchResult(
        entries: List<Entry>
    ): PredicateResult<WordSearchResult.Entry>(entries) {

    class Entry(coordinates: TextCoordinates,
                val word: List<Label>):
            PredicateResult.Entry(coordinates)
    data class Label(val word: String, val stopWord: String)

}
