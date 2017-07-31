package com.codeabovelab.tpc.text

/**
 * Default implementation of [TextCoordinates]
 */
class TextCoordinatesImpl(
        override val offset: Int,
        override val length: Int)
    : TextCoordinates {
    override fun toString(): String {
        return "TextCoordinatesImpl(offset=$offset, length=$length)"
    }
}
