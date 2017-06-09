package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.core.processor.PredicateResult
import com.codeabovelab.tpc.core.processor.RulePredicate
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.deeplearning4j.text.uima.UimaResource
import org.nd4j.linalg.ops.transforms.Transforms
import java.nio.file.Path

import java.util.stream.Collectors

/**
 */
class TextClassifier(
        val vectorsFile: Path,
        val maxLabels: Int,
        val uima: UimaResource,
        val wordSupplier: (wc: WordContext) -> String?
): RulePredicate<TextClassifierResult> {

    val pv: ParagraphVectors

    init {
        pv = WordVectorSerializer.readParagraphVectors(this.vectorsFile.toFile())
        pv.tokenizerFactory = TokenizerFactoryImpl()
    }

    override fun test(pc: PredicateContext, text: Text): TextClassifierResult {
        val si = SentenceIteratorImpl.create(uima, TextIterator.singleton(text))
        val entries = ArrayList<TextClassifierResult.Entry>()
        while(si.hasNext()) {
            val sentence = si.next()
            if(sentence.isNullOrEmpty()) {
                continue
            }
            val seq = sentence!!
            //val vws = words.stream().filter { true || it.pos.isNoun || it.pos.isVerb }.map { VocabWord(1.0, it.str) }.collect(Collectors.toList())
            //println(vws.map { it.word })
            val vws = toWordList(seq)
            val indArray = pv.inferVector(vws)
            //println(indArray)

            val labels = pv.nearestLabels(indArray, maxLabels)
            val labelsWithSim = labels.stream().map {
                val lm = pv.getWordVectorMatrix(it)
                val similarity = Transforms.cosineSim(indArray, lm)
                TextClassifierResult.Label(it, similarity)
            }.collect(Collectors.toList())
            val resEntry = TextClassifierResult.Entry(
                    coordinates = text.getCoordinates(seq.offset, seq.str.length),
                    labels = labelsWithSim)
            entries.add(resEntry)
        }
        return TextClassifierResult(entries = entries)
    }

    private fun toWordList(seq: SentenceData): List<VocabWord> {
        val wch = WordContext.create()
        wch.sentence = seq
        val vws = ArrayList<VocabWord>()
        for (word in seq.words) {
            wch.word = word
            val str = wordSupplier(wch.context)
            if (str.isNullOrEmpty()) {
                continue
            }
            val vw = pv.vocab.wordFor(str)
            // when null - the word is unknown
            if (vw != null) {
                vws.add(vw)
            }
        }
        return vws
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
