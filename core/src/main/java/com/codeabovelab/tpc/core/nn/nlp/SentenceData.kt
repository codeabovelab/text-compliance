package com.codeabovelab.tpc.core.nn.nlp

data class SentenceData(
        override val str: String,
        override val offset: Int, val words: List<WordData>
): StrData