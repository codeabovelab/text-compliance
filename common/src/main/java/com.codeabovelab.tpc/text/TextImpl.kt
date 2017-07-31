package com.codeabovelab.tpc.text

/**
 * Simple text implementation.
 */
class TextImpl(override val data: String) : Text {

    override val length: Int
        get() = data.length
}
