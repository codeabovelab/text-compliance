package com.codeabovelab.tpc.core.doc;

import com.codeabovelab.tpc.core.text.TextConsumer;
import com.codeabovelab.tpc.core.text.TextImpl;
import lombok.Data;

/**
 */
@Data
public final class DocumentFieldImpl implements DocumentField {

    @Data
    public static final class Builder implements DocumentField.Builder {
        private String name;
        private String data;

        public Builder name(String name) {
            setName(name);
            return this;
        }

        public Builder data(String data) {
            setData(data);
            return this;
        }

        @Override
        public DocumentFieldImpl build(Document.Builder document) {
            return new DocumentFieldImpl(document.getId(), this);
        }
    }

    private final String id;
    private final String name;
    private final TextImpl data;

    public DocumentFieldImpl(String parentId, Builder b) {
        this.name = b.name;
        this.id = parentId + this.name;
        this.data = new TextImpl(this.id, b.data);
    }

    @Override
    public void read(TextConsumer consumer) {
        consumer.consume(data);
    }
}
