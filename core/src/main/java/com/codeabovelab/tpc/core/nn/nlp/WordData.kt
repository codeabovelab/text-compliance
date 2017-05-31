package com.codeabovelab.tpc.core.nn.nlp

data class WordData(
        override val str: String,
        override val offset: Int,
        val pos: Pos
): StrData