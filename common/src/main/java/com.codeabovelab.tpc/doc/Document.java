package com.codeabovelab.tpc.doc;

import com.codeabovelab.tpc.text.Textual;

/**
 */
public interface Document extends Textual {
    interface Builder {
        String getId();
        Document build();
    }
}
