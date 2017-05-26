package com.codeabovelab.tpc.text

/**
 * Simple text implementation.
 */
class TextImpl(override val id: String, override val data: String? = null) : Text {

    override val length: Int
        get() = data?.length ?: -1

    override fun getCoordinates(offset: Int, length: Int): TextCoordinates {
        var l = length
        if (length == -1) l = this.length
        return TextCoordinatesImpl(id, offset, l)
    }
}
