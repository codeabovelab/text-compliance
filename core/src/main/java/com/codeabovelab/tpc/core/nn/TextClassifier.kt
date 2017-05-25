package com.codeabovelab.tpc.core.nn;

import com.codeabovelab.tpc.core.processor.PredicateContext;
import com.codeabovelab.tpc.core.processor.PredicateResult;
import com.codeabovelab.tpc.core.processor.RulePredicate;
import com.codeabovelab.tpc.text.Text;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.Collections;
import java.util.stream.Collectors

/**
 */
class TextClassifier(val vectorsFile: String, val maxLabels: Int): RulePredicate<TextClassifierResult> {

    val pv: ParagraphVectors

    init {
        pv = WordVectorSerializer.readParagraphVectors(this.vectorsFile)
        pv.tokenizerFactory = createTokenizerFactory()
    }

    override fun test(pc: PredicateContext, text: Text): TextClassifierResult {
        val indArray = pv.inferVector(text.data.toString())
        val labels = pv.nearestLabels(indArray, maxLabels)
        val labelsWithSim = labels.stream().map {
            val lm = pv.getWordVectorMatrix(it)
            val similarity = Transforms.cosineSim(indArray, lm)
            TextClassifierResult.Label(it, similarity)
        }.collect(Collectors.toList())
        val resEntry = PredicateResult.Entry(text.getCoordinates(0, -1))
        return TextClassifierResult(
                entries = Collections.singletonList(resEntry),
                labels = labelsWithSim
        )
    }
}

fun createTokenizerFactory(): TokenizerFactory {
    var t = DefaultTokenizerFactory()
    t.tokenPreProcessor = CommonPreprocessor()
    return t
}

class TextClassifierResult(
        entries: List<Entry>,
        val labels: List<Label>
    ): PredicateResult(entries) {

    data class Label(val label: String, val similarity: Double)
}
