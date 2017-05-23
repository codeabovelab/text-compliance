package com.codeabovelab.tpc.doc;

import com.codeabovelab.tpc.text.Textual;

/**
 */
public interface DocumentField extends Textual {

    interface Builder {
        DocumentFieldImpl build(Document.Builder document);
    }

    /**
     * Filed name. Note that it not does not include document id.
     * @return non null string
     */
    String getName();
}
