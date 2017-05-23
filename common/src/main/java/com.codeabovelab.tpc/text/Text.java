package com.codeabovelab.tpc.text;

/**
 * Text, note that implementation may be mutable.
 */
public interface Text {
    /**
     * Id of textual.
     * @return non null string
     * @see Textual#getId()
     */
    String getId();

    /**
     * Give current text.
     * @return text
     */
    CharSequence getData();

    /**
     * length of current text
     * @return 0 or positive integer
     */
    int getLength();

    /**
     * Text coordinates, you can supply zero, and -1 when want select all current text.
     * @param offset 0 or positive value less than {@link #getLength()}
     * @param length -1 for all, or any positive value less than '{@link #getLength()} - offset'
     * @return coordinates object
     */
    TextCoordinates getCoordinates(int offset, int length);
}
