package com.codeabovelab.tpc.core.nn.nlp

import com.codeabovelab.tpc.text.Text

/**
 */
interface TextIterator : Iterator<Text> {
    val count: Int

    val labels: List<String>?
    val index: Int

    companion object {
        fun singleton(src: Text): TextIterator {
            return object : TextIterator {

                private var _index = 0

                override val count: Int
                    get() = 1
                override val labels: List<String>?
                    get() = null
                override val index: Int
                    get() = _index

                override fun hasNext() = _index < count

                override fun next(): Text {
                    if(_index != 0) {
                        throw IllegalStateException("Index out of bound exception: $_index, count=$count")
                    }
                    _index++
                    return src
                }

            }
        }
    }
}