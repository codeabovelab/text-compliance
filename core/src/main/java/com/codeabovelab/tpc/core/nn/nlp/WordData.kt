package com.codeabovelab.tpc.core.nn.nlp

data class WordData(
        override val str: String,
        override val offset: Int,
        val stem: String?,
        val lemma: String?,
        val pos: Pos
): StrData