package com.codeabovelab.tpc.text

/**
 * Text coordinates in [Textual] implementation.
 * Usually it offset and length, but for images or multi page document it may be more complex.
 */
interface TextCoordinates {
    /**
     * If of textual object which contains this coordinates.
     * @return non null string
     */
    val textualId: String

    /**
     * Offset from document begin.
     * @return offset
     */
    val offset: Int

    /**
     * Length, can be 0.
     * @return length
     */
    val length: Int
}
