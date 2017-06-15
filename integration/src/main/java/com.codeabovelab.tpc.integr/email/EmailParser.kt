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
    private val QUOTED_REGEX = Pattern.compile("(>+)")
    private val MULTI_QUOTE_HDR_REGEX = Pattern.compile("(?!On.*On\\s.+?wrote:)(On\\s(.+?)wrote:)", Pattern.MULTILINE or Pattern.DOTALL)

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
        var content = NEW_LINE.trimFrom(line)
        val fragment = context.fragment
        if (SIG_REGEX.matcher(content).lookingAt()) {
            content = NEW_LINE.trimLeadingFrom(content)
        }
        val isQuoted = QUOTED_REGEX.matcher(content).lookingAt()

        if (fragment != null && isStringEmpty(content) &&
                SIG_REGEX.matcher(fragment.lines[fragment.lines.size - 1]).lookingAt()) {
            fragment.signature(true)
            finishFragment(context)
        }
        if (fragment != null &&
                (fragment.quoted == isQuoted || fragment.quoted && (quoteHeader(content) || isStringEmpty(content)))) {
            fragment.lines.add(content)
        } else {
            finishFragment(context)
            context.fragment = Fragment.build { quoted = isQuoted }.line(content)
        }

    }

    private fun quoteHeader(line: String): Boolean {
        val reversed = StringBuilder(line).reverse().toString()
        return QUOTE_HDR_REGEX.matcher(reversed).lookingAt()
    }

    private fun finishFragment(context: Context) {
        if (context.fragment != null && !isStringEmpty(context.fragment!!.text())) {
            val fragment = context.fragment!!
            if (fragment.quoted || fragment.signature || fragment.lines.isEmpty()) {
                fragment.hidden(true)
            }
            context.fragments.add(fragment.build())
        }

        context.fragment = null
    }

    private fun isStringEmpty(content: String): Boolean {
        return content.isNullOrBlank()
    }

    class Context(
            val text: String,
            var fragment: Builder? = null,
            var fragments: MutableList<Fragment> = Lists.newArrayList()
    )

}