package com.codeabovelab.tpc.integr.email

import com.google.common.collect.Lists
import java.util.stream.Collectors


class Fragment(val content: String, val hidden: Boolean, val signature: Boolean, val quoted: Boolean) {

    class Builder {
        var hidden: Boolean = false
        var signature: Boolean = false
        var quoted: Boolean = false
        val lines: MutableList<String> = Lists.newArrayList()

        fun hidden(isHidden: Boolean) = apply { this.hidden = isHidden }
        fun signature(isSignature: Boolean) = apply { this.signature = isSignature }
        fun quoted(isQuoted: Boolean) = apply { this.quoted = isQuoted }
        fun line(line: String) = apply { this.lines.add(line) }

        fun text(): String {
            return Lists.reverse(lines).stream().collect(Collectors.joining("\n"))
        }

        fun build() = Fragment(text(), hidden, signature, quoted)
    }

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block)
    }

    override fun toString(): String {
        return "Fragment(content='$content', hidden=$hidden, signature=$signature, quoted=$quoted)"
    }

}