package com.codeabovelab.tpc.core.doc;

import com.codeabovelab.tpc.core.text.Textual;

/**
 */
public interface Document extends Textual {
    interface Builder {
        String getId();
        Document build();
    }
}
