package com.codeabovelab.tpc.core.text;

/**
 * Something that has text.
 */
public interface Textual {

    /**
     * Unique id of textual. May be uuid or something else.
     * @return non null string
     */
    String getId();

    /**
     * Sequentially invoke consumer on internal text.
     * @param consumer non null value
     */
    void read(TextConsumer consumer);
}
