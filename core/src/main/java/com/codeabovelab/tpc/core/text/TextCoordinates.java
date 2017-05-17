package com.codeabovelab.tpc.core.text;

/**
 * Text coordinates in {@link Textual } implementation.
 * Usually it offset and length, but for images or multi page document it may be more complex.
 */
public interface TextCoordinates {
    /**
     * If of textual object which contains this coordinates.
     * @return non null string
     */
    String getTextualId();

    /**
     * Offset from document begin.
     * @return offset
     */
    int getOffset();

    /**
     * Length, can be 0.
     * @return length
     */
    int getLength();
}
