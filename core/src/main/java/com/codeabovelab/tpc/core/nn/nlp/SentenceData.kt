package com.codeabovelab.tpc.core.nn.nlp

data class SentenceData(
        override val str: String,
        override val offset: Int, val words: List<WordData>
): StrData

/**
 * Return true when it null or internal sentence string is null or empty
 */
fun SentenceData?.isNullOrEmpty(): Boolean {
    return this == null || this.str.isNullOrEmpty()
}
