package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Textual

/**
 */
interface DocumentField : Textual {

    interface Builder<B : Builder<B>>: Textual.Builder<B> {
        override fun build(): DocumentField
    }
}
