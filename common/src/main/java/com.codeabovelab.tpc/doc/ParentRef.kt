package com.codeabovelab.tpc.doc

/**
 * A reference to parent document. Unlike simple ref, it
 * may has reference to its own parent document.
 */
class ParentRef(
        id: String,
        /**
         * Reference to parent. Null mean that we can not known anything about parent existing.
         * Empty - mean that ref have not parent.
         */
        val parent: List<ParentRef>? = null
): Ref(id)