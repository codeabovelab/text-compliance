package com.codeabovelab.tpc.core.kw

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
        val uima: UimaResource
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
            val labels = extractLabels(seq)
            if(labels.isEmpty()) {
                return WordSearchResult(entries = emptyList())
            }
            val resEntry = WordSearchResult.Entry(
                    coordinates = text.getCoordinates(seq.offset, seq.str.length),
                    word = labels)
            entries.add(resEntry)
        }
        return WordSearchResult(entries = entries)
    }

    private fun extractLabels(seq: SentenceData): List<WordSearchResult.Label> {
        val wordContext = WordContext.create()
        wordContext.sentence = seq
        val resultList = ArrayList<WordSearchResult.Label>()
        for (word in seq.words) {
            wordContext.word = word
            val str = wordContext.context.word.str
            if (str.isNullOrEmpty()) {
                continue
            }
            val labels = keywordMatcher.test(wordContext.context)
            if (labels.isEmpty()) {
                continue
            }
            resultList.add(WordSearchResult.Label(str, labels))
        }
        return resultList
    }
}

class WordSearchResult(
        entries: List<Entry>
    ): PredicateResult<WordSearchResult.Entry>(entries) {

    class Entry(coordinates: TextCoordinates,
                val word: List<Label>):
            PredicateResult.Entry(coordinates)
    data class Label(val word: String, val labels: Set<String>)

}
