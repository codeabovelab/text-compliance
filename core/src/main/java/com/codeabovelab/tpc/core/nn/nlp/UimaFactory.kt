package com.codeabovelab.tpc.core.nn.nlp

import org.apache.uima.UIMAFramework
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.cleartk.clearnlp.MpAnalyzer
import org.cleartk.clearnlp.PosTagger
import org.cleartk.clearnlp.Tokenizer
import org.cleartk.opennlp.tools.SentenceAnnotator
import org.deeplearning4j.text.uima.UimaResource

/**
 */
object UimaFactory {

    fun create(req: Request): UimaResource {
        // note that MpAnalyzer require POS, therefore we must enable both them
        val args = if(req.morphological) {
            arrayOf(SentenceAnnotator.getDescription(),
                    Tokenizer.getDescription(),
                    PosTagger.getDescription(),
                    MpAnalyzer.getDescription())
        } else if(req.pos) {
            arrayOf(SentenceAnnotator.getDescription(),
                    Tokenizer.getDescription(),
                    PosTagger.getDescription())
        } else {
            arrayOf(SentenceAnnotator.getDescription(),
                    Tokenizer.getDescription())
        }
        val desc = AnalysisEngineFactory.createEngineDescription(*args)
        val engine = UIMAFramework.produceAnalysisEngine(desc, null, mapOf(
                // it enable safe concurrency, but does not affect performance
                Pair(AnalysisEngine.PARAM_NUM_SIMULTANEOUS_REQUESTS, 1)
        ))
        return UimaResource(engine)
    }

    /**
     * Do not use in production.
     * @see [UimaFactory.create]
     */
    fun create(
            pos: Boolean = true,
            morphological: Boolean = true
    ): UimaResource {
        return create(Request(pos = pos, morphological = morphological))
    }

    data class Request(
            val pos: Boolean = true,
            val morphological: Boolean = true
    ) {
        /**
         * Test that specified request fit with specified, in other words it is do equal or greater processing.
         */
        fun fit(request: Request) : Boolean {
            if(request.morphological) {
                return this.morphological
            }
            if(request.pos) {
                return this.pos || this.morphological
            }
            return true
        }
    }
}
