package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import com.fasterxml.jackson.annotation.JsonTypeName
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet

/**
 */
@JsonTypeName("WordPredicate")
class WordPredicate(
        private val keywordMatcher: KeywordMatcher
): RulePredicate<WordSearchResult> {

    override fun test(pc: PredicateContext, text: Text): WordSearchResult {
        val si = pc.sentenceIterator(text)
        val entries = ArrayList<WordSearchResult.Entry>()
        val labelsSet = HashSet<Label>()
        val keywords = HashSet<String>()
        while(si.hasNext()) {
            val sentence = si.next()
            if(sentence.isNullOrEmpty()) {
                continue
            }
            val seq = sentence!!
            extractLabels(seq, keywords, labelsSet)
            if(labelsSet.isEmpty() || keywords.isEmpty()) {
                continue
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

    private fun extractLabels(seq: SentenceData, keywords: MutableSet<String>, labelsSet: MutableSet<Label>) {
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
    ) : PredicateResult.Entry(coordinates), Labeled
}
