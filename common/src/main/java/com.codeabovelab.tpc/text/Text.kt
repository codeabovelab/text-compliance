package com.codeabovelab.tpc.text

/**
 * Text, note that implementation may be mutable.
 */
interface Text {
    /**
     * Give current text.
     * @return text
     */
    val data: CharSequence

    /**
     * length of current text
     * @return 0 or positive integer
     */
    val length: Int

    /**
     * Text coordinates, you can supply zero, and -1 when want select all current text.
     * @param offset 0 or positive value less than [.getLength]
     * *
     * @param length -1 for all, or any positive value less than '[.getLength] - offset'
     * *
     * @return coordinates object
     */
    fun getCoordinates(offset: Int, length: Int): TextCoordinates {
        var l = length
        if (length == -1) l = this.length
        return TextCoordinatesImpl(offset, l)
    }
}
