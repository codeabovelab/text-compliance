package com.codeabovelab.tpc.core.doc;

import com.codeabovelab.tpc.core.text.TextConsumer;
import com.codeabovelab.tpc.core.text.TextImpl;
import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Data
public final class DocumentImpl implements Document {

    @Data
    public final static class Builder implements Document.Builder {
        private String id;
        private String body;
        private final List<DocumentField.Builder> fields = new ArrayList<>();

        public Builder id(String id) {
            setId(id);
            return this;
        }

        public Builder body(String body) {
            setBody(body);
            return this;
        }

        public Builder addField(DocumentField.Builder field) {
            this.fields.add(field);
            return this;
        }

        @Override
        public DocumentImpl build() {
            return new DocumentImpl(this);
        }
    }

    private final String id;
    private final TextImpl body;
    private final List<DocumentField> fields;

    public DocumentImpl(Builder b) {
        this.id = b.id;
        this.body = new TextImpl(this.id, b.body);
        ImmutableList.Builder<DocumentField> fb = ImmutableList.builder();
        b.fields.forEach(f -> fb.add(f.build(b)));
        this.fields = fb.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void read(final TextConsumer consumer) {
        consumer.consume(body);
        fields.forEach(df -> df.read(consumer));
    }
}
