package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
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
        val labelsSet = HashSet<Label>()
        val keywords = ArrayList<String>()
        while(si.hasNext()) {
            val sentence = si.next()
            if(sentence.isNullOrEmpty()) {
                continue
            }
            val seq = sentence!!
            extractLabels(seq, keywords, labelsSet)
            if(labelsSet.isEmpty() || keywords.isEmpty()) {
                return WordSearchResult(entries = emptyList())
            }
            val resEntry = WordSearchResult.Entry(
                    coordinates = text.getCoordinates(seq.offset, seq.str.length),
                    keywords = ImmutableList.copyOf(keywords),
                    labels = ImmutableSet.copyOf(labelsSet))
            keywords.clear()
            labelsSet.clear()
            entries.add(resEntry)
        }
        return WordSearchResult(entries = entries)
    }

    private fun extractLabels(seq: SentenceData, keywords: MutableList<String>, labelsSet: MutableSet<Label>) {
        val wordContext = WordContext.create()
        wordContext.sentence = seq
        for (word in seq.words) {
            wordContext.word = word
            val str = wordContext.context.word.str
            if (str.isNullOrEmpty()) {
                continue
            }
            val wordLabels = keywordMatcher.test(wordContext.context)
            if(wordLabels.isEmpty()) {
                continue
            }
            keywords.add(str)
            wordLabels.forEach {
                labelsSet.add(Label(it, 1.0))
            }
        }
    }
}

class WordSearchResult(
        entries: List<Entry>
    ): PredicateResult<WordSearchResult.Entry>(entries) {

    class Entry(coordinates: TextCoordinates,
                val keywords: List<String>,
                override val labels: Collection<Label>
    ) : PredicateResult.Entry(coordinates), Labeled {
    }
}
