package com.codeabovelab.tpc.integr.email

import com.codeabovelab.tpc.util.RegexMatcher
import com.google.common.base.CharMatcher
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import java.util.*
import java.util.regex.Pattern



class EmailParser {

    private val FLAGS = Pattern.MULTILINE or Pattern.DOTALL or Pattern.CASE_INSENSITIVE
    private val CRLF = CharMatcher.anyOf("\r\n")

    // temporary disable signature detection
    //private val SIG_REGEX = Pattern.compile("(\u2014|--|__|-\\w)|(^Sent from my (\\w+\\s*){1,3})")
    private val LINE_DELIM_STR = "\\s*-{3,}"
    private val HEADER_PART_STR =
            "-+original\\s+message-+|" +
            "-+\\s*forwarded[^-]*|" +
            "(?:cc|subject|from|to):"
    private val HEADER_STR = "On\\s(?:[^\\n]+)wrote:|" +
            "^[^<\n]+<[^@<]+@[^@>]+>\\s+on\\s+[^\\n]+"
    private val LINE_HEADER_RM = RegexMatcher("^($HEADER_STR)", FLAGS)
    private val QUOTE_MARKER_RX = Pattern.compile("^(>|$HEADER_STR|$HEADER_PART_STR|$LINE_DELIM_STR)", FLAGS)
    private val LINE_DELIM_RM = RegexMatcher("^($LINE_DELIM_STR)", FLAGS)
    private val HEADER_PART_RM = RegexMatcher("^($HEADER_STR|$HEADER_PART_STR)", FLAGS)

    fun read(content: String): Email {
        val text = CRLF.replaceFrom(content, "\n")
        val context = Context(text)
        context.read()
        return Email(ImmutableList.copyOf(context.fragments))
    }

    private inner class Context(
            private var text: String
    ) {
        val stack = ArrayDeque<Fragment.Builder>()
        var fragments: MutableList<Fragment> = Lists.newArrayList()
        val currHeader = HeaderMark()
        val prevHeader = HeaderMark()


        private fun finishFragment() {
            val builder = stack.pollLast()
            if(builder == null) {
                return
            }
            val frag = builder.build()
            if (!frag.content.isNullOrEmpty()) {
                fragments.add(frag)
            }
        }

        private fun addPart(header: HeaderMark, part: String) {
            finishFragment()
            stack.addLast(Fragment.builder {
                quoted = header.isFound()
                text = part
            })
        }


        private var prev = 0

        fun read() {
            val m = QUOTE_MARKER_RX.matcher(text)
            var last: Int
            while(m.find()) {
                last = m.start()
                var group = m.group()
                when(group) {
                    in LINE_HEADER_RM.lookingAt() -> {
                        //it full header in one line
                        //but may be prepend with line delimiter
                        if(currHeader.lineHeader != -1) {
                            partComplete()
                        }
                        currHeader.lineHeader = last
                    }
                    in HEADER_PART_RM.lookingAt() -> {
                        val partType = detectType(group)
                        if(partType != null) {
                            //do not use map.compute here!
                            val parts = currHeader.headerParts
                            if(partType == HeaderPartType.FORWARD && parts.isNotEmpty()) {
                                // forward must be a first part of header, otherwise it another header
                                partComplete()
                                parts.put(partType, last)
                            } else {
                                val old = parts.putIfAbsent(partType, last)
                                if(old != null) {
                                    partComplete()
                                    parts.put(partType, last)
                                }
                            }
                        }
                    }
                    in LINE_DELIM_RM.matches() -> {
                        //TODO we need see previous lines after found header,
                        // and when it lines contains ----- or name & date then we must extend header for it
                    }
                    else -> {
                        if(currHeader.quote == -1) {
                            //due to quote may appeared many times we do not invoke partComplete()
                            // but record only first appearing
                            currHeader.quote = last
                        }
                    }
                }
            }
            partComplete()
            if(prevHeader.isFound()) {
                addPart(prevHeader, text.substring(prev))
            }
            finishFragment()
        }

        private fun detectType(group: String): HeaderPartType? {
            if(group.startsWith("-")) {
                return HeaderPartType.FORWARD
            }
            val lowered = group.toLowerCase()
            if(lowered.startsWith("from:")) {
                return HeaderPartType.FROM
            }
            if(lowered.startsWith("to:")) {
                return HeaderPartType.TO
            }
            if(lowered.startsWith("cc:")) {
                return HeaderPartType.CC
            }
            if(lowered.startsWith("subject:")) {
                return HeaderPartType.SUBJECT
            }
            return null
        }

        private fun partComplete() {
            val current = if(currHeader.isFound()) currHeader.getBegin() else text.length
            // note that it part correspond to prevHeader
            addPart(prevHeader, text.substring(prev, current))
            prevHeader.copy(currHeader)
            this.prev = current
            currHeader.clear()
        }
    }

    enum class HeaderPartType {
        CC,
        FROM,
        TO,
        SUBJECT,
        /**
         * It -- original message -- or -- Forwarded -- & etc
         */
        FORWARD
    }

    class HeaderMark {
        var lineHeader = -1
        var headerParts = HashMap<HeaderPartType, Int>()
        var lineDelim = -1
        var quote = -1

        fun clear() {
            lineHeader = -1
            headerParts.clear()
            lineDelim = -1
            quote = -1
        }

        fun isFound(): Boolean {
            return lineHeader >= 0 || headerParts.isNotEmpty() || lineDelim >= 0 || quote >= 0
        }

        fun copy(orig: HeaderMark) {
            this.lineHeader = orig.lineHeader
            this.headerParts.clear()
            this.headerParts.putAll(orig.headerParts)
            this.lineDelim = orig.lineDelim
            this.quote = orig.quote
        }

        fun getBegin(): Int {
            var offset = calcMin { min ->
                min(lineHeader)
                min(lineDelim)
                min(quote)
                headerParts.forEach { _, pos -> min(pos) }
            }
            if(offset == Int.MAX_VALUE) {
                // header not found use 0 as it's start
                offset = 0
            }
            return offset
        }

        fun getTextBegin(): Int {
            return calcMin { min ->
                min(lineHeader)
                min(quote)
                headerParts.forEach { _, pos -> min(pos)}
            }
        }

        inline fun calcMin(op: (min: (Int) -> Unit) -> Unit): Int {
            var offset = Int.MAX_VALUE
            val min = { arg: Int -> if(arg > -1 && arg < offset) offset = arg }
            op(min)
            return offset
        }
    }

}
