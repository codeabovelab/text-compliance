package com.codeabovelab.tpc.integr.email

import com.google.common.collect.Lists
import java.util.stream.Collectors


class Fragment(val content: String, val isHidden: Boolean, val isSignature: Boolean, val isQuoted: Boolean) {

    class Builder {
        var isHidden: Boolean = false
        var isSignature: Boolean = false
        var isQuoted: Boolean = false
        val lines: MutableList<String> = Lists.newArrayList()

        fun isHidden(isHidden: Boolean) = apply { this.isHidden = isHidden }
        fun isSignature(isSignature: Boolean) = apply { this.isSignature = isSignature }
        fun isQuoted(isQuoted: Boolean) = apply { this.isQuoted = isQuoted }
        fun line(line: String) : Builder {
            this.lines.add(line)
            return this
        }
        fun text() : String {
            return Lists.reverse(lines).stream().collect(Collectors.joining("\n"))
        }

        companion object {
            inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
        }

        fun build() = Fragment(text(), isHidden, isSignature, isQuoted)
    }

}