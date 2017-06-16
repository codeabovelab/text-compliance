package com.codeabovelab.tpc.integr.email

class Fragment(val content: String, val hidden: Boolean, val signature: Boolean, val quoted: Boolean) {

    class Builder {
        var hidden: Boolean = false
        var signature: Boolean = false
        var quoted: Boolean = false
        var text: String? = null

        fun hidden(isHidden: Boolean) = apply { this.hidden = isHidden }
        fun signature(isSignature: Boolean) = apply { this.signature = isSignature }
        fun quoted(isQuoted: Boolean) = apply { this.quoted = isQuoted }

        fun build(): Fragment {
            if (quoted || signature) {
                hidden = true
            }
            return Fragment(text!!, hidden, signature, quoted)
        }
    }

    companion object {
        inline fun builder(block: Builder.() -> Unit) = Builder().apply(block)
    }

    override fun toString(): String {
        return "Fragment(content='$content', hidden=$hidden, signature=$signature, quoted=$quoted)"
    }

}