package com.codeabovelab.tpc.core.nn;

import com.codeabovelab.tpc.core.nn.nlp.SentenceIteratorImpl
import com.codeabovelab.tpc.core.nn.nlp.TextIterator
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.core.processor.PredicateResult
import com.codeabovelab.tpc.core.processor.RulePredicate
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.nd4j.linalg.ops.transforms.Transforms

import java.util.stream.Collectors

/**
 */
class TextClassifier(val vectorsFile: String, val maxLabels: Int): RulePredicate<TextClassifierResult> {

    val pv: ParagraphVectors

    init {
        pv = WordVectorSerializer.readParagraphVectors(this.vectorsFile)
        pv.tokenizerFactory = TokenizerFactoryImpl()
    }

    override fun test(pc: PredicateContext, text: Text): TextClassifierResult {
        val si = SentenceIteratorImpl.create(TextIterator.singleton(text))
        var entries = ArrayList<TextClassifierResult.Entry>()
        while(si.hasNext()) {
            val sentence = si.nextSentence()
            if(sentence.isNullOrEmpty()) {
                continue
            }
            val words = si.currentWords()!!
            val vws = words.stream().filter { true || it.pos.isNoun || it.pos.isVerb }.map { VocabWord(1.0, it.str) }.collect(Collectors.toList())
            //println(vws.map { it.word })
            //TODO commented code is produce incorrect vector (full of zeros)!!!! how to build vector for words?
            //val indArray = pv.inferVector(vws)
            val indArray = pv.inferVector(sentence)
            //println(indArray)

            val labels = pv.nearestLabels(indArray, maxLabels)
            //val labels = pv.wordsNearest(indArray, maxLabels)
            val labelsWithSim = labels.stream().map {
                val lm = pv.getWordVectorMatrix(it)
                val similarity = Transforms.cosineSim(indArray, lm)
                TextClassifierResult.Label(it, similarity)
            }.collect(Collectors.toList())
            val resEntry = TextClassifierResult.Entry(
                    coordinates = text.getCoordinates(si.currentOffset()!!, sentence!!.length),
                    labels = labelsWithSim)
            entries.add(resEntry)
        }
        return TextClassifierResult(entries = entries)
    }
}

class TextClassifierResult(
        entries: List<Entry>
    ): PredicateResult<TextClassifierResult.Entry>(entries) {

    class Entry(coordinates: TextCoordinates,
                val labels: List<Label>):
            PredicateResult.Entry(coordinates)

    data class Label(val label: String, val similarity: Double)
}
