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
): Ref(id) {

    override fun toString(): String {
        val sb = StringBuilder("ParentRef(")
        sb.append(id)
        if(parent != null) {
            sb.append(" -> ")
            parent.joinTo(sb)
        }
        return sb.append(')').toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParentRef) return false

        if (id != other.id) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }
}