package com.codeabovelab.tpc.integr.email

import com.codeabovelab.tpc.integr.email.Fragment.Builder
import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import java.util.regex.Pattern


class EmailParser {

    private val NEW_LINE = CharMatcher.anyOf("\n")
    private val CRLF = CharMatcher.anyOf("\r\n")

    private val SIG_REGEX = Pattern.compile("(\u2014|--|__|-\\w)|(^Sent from my (\\w+\\s*){1,3})")
    private val QUOTE_HDR_REGEX = Pattern.compile("^:etorw.*nO")
    private val MULTI_QUOTE_HDR_REGEX = Pattern.compile("(?!On.*On\\s.+?wrote:)(On\\s(.+?)wrote:)", Pattern.MULTILINE or Pattern.DOTALL)
    private val QUOTED_REGEX = Pattern.compile("(>+)")

    fun read(content: String): Email {
        val context = Context(text = CRLF.replaceFrom(content, "\n"))
        read(context)
        return Email(ImmutableList.copyOf(Lists.reverse(context.fragments)))
    }

    fun read(context: Context) {
        var workingText = context.text
        val multiQuote = Pattern.compile(MULTI_QUOTE_HDR_REGEX.pattern(), Pattern.MULTILINE or Pattern.DOTALL)
                .matcher(workingText)

        if (multiQuote.find()) {
            val newQuoteHeader = NEW_LINE.replaceFrom(multiQuote.group(), "")
            workingText = Pattern.compile(MULTI_QUOTE_HDR_REGEX.pattern(), Pattern.DOTALL).matcher(workingText).replaceAll(
                    newQuoteHeader)
        }

        Lists.reverse(Splitter.on('\n').splitToList(workingText))
                .stream()
                .forEach { l -> scanLine(l, context) }

        finishFragment(context)

    }

    private fun scanLine(line: String, context: Context) {
        var line = line
        line = NEW_LINE.trimFrom(line)
        if (SIG_REGEX.matcher(line).lookingAt()) {
            line = NEW_LINE.trimLeadingFrom(line)
        }
        val isQuoted = QUOTED_REGEX.matcher(line).lookingAt()

        if (context.fragment != null && isStringEmpty(line)) {
            if (SIG_REGEX.matcher(context.fragment!!.lines[context.fragment!!.lines.size - 1]).lookingAt()) {
                context.fragment!!.isSignature(true)
                finishFragment(context)
            }
        }

        if (context.fragment != null && (context.fragment!!.isQuoted == isQuoted
                || context.fragment!!.isQuoted
                && (quoteHeader(line) || isStringEmpty(line)))) {
            context.fragment!!.lines.add(line)
        } else {
            finishFragment(context)
            context.fragment = Fragment.Builder().isQuoted(isQuoted).line(line)
        }

    }

    private fun quoteHeader(line: String): Boolean {
        val reversed = StringBuffer(line).reverse().toString()
        return QUOTE_HDR_REGEX.matcher(reversed).lookingAt()
    }

    private fun finishFragment(context: Context) {
        if (context.fragment != null && !isStringEmpty(context.fragment!!.text())) {
            if (context.fragment!!.isQuoted ||
                    context.fragment!!.isSignature ||
                    context.fragment!!.lines.isEmpty()) {
                context.fragment!!.isHidden(true)
            }
            val build = context.fragment!!.build()
            context.fragments.add(build)
        }

        context.fragment = null
    }

    private fun isStringEmpty(content: String): Boolean {
        return content.trim().isEmpty()
    }

    class Context(val text: String, var fragment: Builder? = null, var fragments: MutableList<Fragment> = Lists.newArrayList())

}