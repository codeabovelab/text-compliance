package com.codeabovelab.tpc.integr.email

import com.google.common.base.CharMatcher
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import java.util.*
import java.util.regex.Pattern


class EmailParser {

    private val CRLF = CharMatcher.anyOf("\r\n")

    // temporary disable signature detection
    //private val SIG_REGEX = Pattern.compile("(\u2014|--|__|-\\w)|(^Sent from my (\\w+\\s*){1,3})")
    private val HEADER_STR ="-+\\s*forwarded[^-]*-+|" +
            "-+original\\s+message-+|" +
            "On\\s(?:.+)wrote:|" +
            "(?:--+)\n(?:from|to):|" +
            "[^<\n]+<.+@.+>\\s+on\\s+.+|" +
    //TODO below regex must consume two previous string
            "^to:.*^cc:.*^subject:"
    //
    private val LINE_HEADER_RX = Pattern.compile("^($HEADER_STR)", Pattern.MULTILINE or Pattern.DOTALL or Pattern.CASE_INSENSITIVE)
    private val QUOTE_MARKER_RX = Pattern.compile("^(>|$HEADER_STR)", Pattern.MULTILINE or Pattern.DOTALL or Pattern.CASE_INSENSITIVE)

    fun read(content: String): Email {
        val context = Context()
        val text = CRLF.replaceFrom(content, "\n")
        read(text, context)
        return Email(ImmutableList.copyOf(context.fragments))
    }

    private fun read(text: String, context: Context) {
        val m = QUOTE_MARKER_RX.matcher(text)
        var offset = 0
        while(m.find()) {
            val start = m.start()
            context.scanPart(text.substring(offset, start))
            offset = start
        }
        context.scanPart(text.substring(offset))
        context.finishFragment()
    }

    private inner class Context {
        val stack = ArrayDeque<Fragment.Builder>()
        var fragments: MutableList<Fragment> = Lists.newArrayList()
        val current: Fragment.Builder?
            get() = stack.peekLast()

        var isQuoted: Boolean = false
        var isHeader: Boolean = false
        var currentLine: String = ""
            set(value) {
                field = value
                isQuoted = isQuoted(field)
                isHeader = isHeader(field)
            }

        private fun isQuoted(line: String) = line.first() == '>'
        private fun isHeader(line: String): Boolean {
            return LINE_HEADER_RX.matcher(line).lookingAt()
        }

        /**
         * test that current line is suitable with current fragment
         */
        fun isSuitable(): Boolean {
            if(this.current == null) {
                return false
            }
            if(isHeader) {
                return false
            }
            return current!!.quoted == isQuoted || currentLine.isNullOrEmpty()
        }

        fun finishFragment() {
            val builder = stack.pollLast()
            if(builder == null) {
                return
            }
            val frag = builder!!.build()
            if (!frag.content.isNullOrEmpty()) {
                fragments.add(frag)
            }
        }

        fun newFragment() {
            stack.addLast(Fragment.builder {
                quoted = isQuoted || isHeader
                parts.append(currentLine)
            })
        }

        fun scanPart(line: String) {
            if(line.isEmpty()) {
                return
            }
            currentLine = line
            val fragment = current
            if (fragment != null && isSuitable()) {
                fragment.parts.append(currentLine)
            } else {
                finishFragment()
                newFragment()
            }
        }

    }

}
