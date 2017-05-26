package com.codeabovelab.tpc.text

/**
 * Default implementation of [TextCoordinates]
 */
class TextCoordinatesImpl(
        override val textualId: String,
        override val offset: Int,
        override val length: Int)
    : TextCoordinates {
    override fun toString(): String {
        return "TextCoordinatesImpl(textualId='$textualId', offset=$offset, length=$length)"
    }
}
