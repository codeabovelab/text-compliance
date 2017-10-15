package com.codeabovelab.tpc.core.nn.sentiment

import com.codeabovelab.tpc.core.nn.nlp.Pos
import org.apache.uima.fit.util.JCasUtil
import org.cleartk.token.type.Token
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer
import org.deeplearning4j.text.tokenization.tokenizerfactory.UimaTokenizerFactory
import org.deeplearning4j.text.uima.UimaResource
import java.util.*

/**
 * sometimes gives slight better result then default
 */
class UimaTokenizerFactoryF(resource: UimaResource) : UimaTokenizerFactory(resource) {

    override fun create(toTokenize: String?): Tokenizer {
        return UimaTokenizer(toTokenize!!, uimaResource, tokenPreProcessor)
    }


    class UimaTokenizer(tokens: String, resource: UimaResource, var preProcess: TokenPreProcess) : Tokenizer {

        private val tokens: List<String>
        private var index: Int = 0

        init {
            val cas = resource.process(tokens)
            try {
                val map = Pos.values().map { it.name }
                val tokenList = JCasUtil.select(cas.jCas, Token::class.java)
                this.tokens = tokenList
                        .filter { !it.lemma.isNullOrEmpty() }
                        .filter { !it.pos.isNullOrEmpty() }
                        .filter { map.contains(it.pos) }
                        .map { it.lemma }

            } catch (e: Exception) {
                throw RuntimeException(e)
            } finally {
                resource.release(cas)
            }

        }

        override fun hasMoreTokens(): Boolean {
            return index < tokens.size
        }

        override fun countTokens(): Int {
            return tokens.size
        }

        override fun nextToken(): String {
            var ret = tokens[index]
            index++
            if (preProcess != null)
                ret = preProcess.preProcess(ret)
            return ret
        }

        override fun getTokens(): List<String> {
            val tokens = ArrayList<String>()
            while (hasMoreTokens()) {
                tokens.add(nextToken())
            }
            return tokens
        }

        override fun setTokenPreProcessor(tokenPreProcessor: TokenPreProcess) {
            this.preProcess = tokenPreProcessor
        }

    }

}
