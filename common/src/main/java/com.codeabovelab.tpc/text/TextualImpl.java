package com.codeabovelab.tpc.text;

/**
 */
public final class TextualImpl implements Textual {
    private final String id;
    private final TextImpl text;

    public TextualImpl(String id, String text) {
        this.id = id;
        this.text = new TextImpl(this.id, text);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(TextConsumer consumer) {
        consumer.consume(text);
    }
}
