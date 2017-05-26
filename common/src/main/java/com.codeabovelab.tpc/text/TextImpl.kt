package com.codeabovelab.tpc.text

/**
 * Simple text implementation.
 */
class TextImpl(override val id: String, override val data: String? = null) : Text {

    override val length: Int
        get() = data?.length ?: -1

    override fun getCoordinates(offset: Int, len: Int): TextCoordinates {
        var l = len
        if (len == -1) l = length
        return TextCoordinatesImpl(id, offset, l)
    }
}
