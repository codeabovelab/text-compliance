package com.codeabovelab.tpc.core.nn

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import java.util.regex.Pattern

/**
 */

fun TokenizerFactoryImpl(): TokenizerFactory {
    val tf = DefaultTokenizerFactory()
    tf.tokenPreProcessor = TokenPreProcessImpl()
    return tf
}


class TokenPreProcessImpl: TokenPreProcess {
    private val pattern = Pattern.compile("[^\\w]+", Pattern.UNICODE_CHARACTER_CLASS)

    override fun preProcess(token: String?): String {
        val res = pattern.matcher(token).replaceAll("").toLowerCase()
        return res
    }
}

