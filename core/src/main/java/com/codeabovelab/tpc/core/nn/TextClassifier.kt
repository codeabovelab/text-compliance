package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.nn.nlp.*
import com.codeabovelab.tpc.core.processor.*
import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextCoordinates
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors
import org.deeplearning4j.models.word2vec.VocabWord
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.ops.transforms.Transforms
import java.nio.file.Path

import java.util.stream.Collectors

/**
 */
@JsonTypeName("TextClassifierPredicate")
class TextClassifier(
        private val vectorsFile: Path,
        private val maxLabels: Int,
        private val wordSupplier: (wc: WordContext) -> String?
): RulePredicate<TextClassifierResult> {

    private val pv: ParagraphVectors

    init {
        pv = WordVectorSerializer.readParagraphVectors(this.vectorsFile.toFile())
        pv.unk = UnkDetector.UNK
        pv.tokenizerFactory = tokenizerFactory()
    }

    override fun test(pc: PredicateContext, text: Text): TextClassifierResult {
        val si = pc.sentenceIterator(text)
        val entries = ArrayList<TextClassifierResult.Entry>()
        var count = 0
        var sum: INDArray? = null
        while(si.hasNext()) {
            val sentence = si.next()
            if(sentence.isNullOrEmpty()) {
                continue
            }
            val seq = sentence!!
            val vws = toWordList(seq)
            if(vws.isEmpty()) {
                continue
            }
            count++
            val indArray = pv.inferVector(vws)
            sum = if(sum == null) indArray.dup() else sum.addi(indArray)
            val seqLabels = extractLabels(indArray)
            val resEntry = TextClassifierResult.Entry(
                    coordinates = text.getCoordinates(seq.offset, seq.str.length),
                    labels = seqLabels)
            entries.add(resEntry)
        }
        // we compute average value of all sentence vectors, it is differ
        // from simple vector of all text
        val textLabels = if(sum == null) listOf() else extractLabels(sum.div(count))
        return TextClassifierResult(entries = entries, labels = textLabels)
    }

    private fun extractLabels(indArray: INDArray): List<Label> {
        val labels = pv.nearestLabels(indArray, maxLabels)
        val labelsWithSim = labels.stream().map {
            val lm = pv.getWordVectorMatrix(it)
            val similarity = Transforms.cosineSim(indArray, lm)
            Label(it, similarity)
        }.collect(Collectors.toList())
        return labelsWithSim
    }

    private fun toWordList(seq: SentenceData): List<VocabWord> {
        val wch = WordContext.create()
        wch.sentence = seq
        val vws = ArrayList<VocabWord>()
        for (word in seq.words) {
            wch.word = word
            val str: String?
            if(UnkDetector.isUnknown(word)) {
                str = UnkDetector.UNK
            } else {
                str = wordSupplier(wch.context)
                if (str.isNullOrEmpty()) {
                    continue
                }
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
        entries: List<Entry>,
        /**
         * Labels for all text
         */
        override val labels: Collection<Label>
    ): PredicateResult<TextClassifierResult.Entry>(entries), Labeled {

    class Entry(
            coordinates: TextCoordinates,
            override val labels: List<Label>
    ) : PredicateResult.Entry(coordinates), Labeled
}
