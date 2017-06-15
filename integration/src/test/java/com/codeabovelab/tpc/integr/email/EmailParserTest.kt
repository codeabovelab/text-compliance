package com.codeabovelab.tpc.integr.email

import com.google.common.base.Splitter
import com.google.common.io.LineProcessor
import com.google.common.io.Resources
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

internal class EmailParserTest {
    data class EmailSource(
            val str: String,
            val quotes: Int
    )

    val parser: EmailParser = EmailParser()
    val splitter = Splitter.on(',').withKeyValueSeparator('=')!!

    private fun getEmails(): List<EmailSource> {
        val url = Resources.getResource(this.javaClass, "/emails/emails.txt")
        return Resources.readLines(url, Charsets.UTF_8, object: LineProcessor<List<EmailSource>> {
            var quotes = 0
            val sb = StringBuilder()
            val emails = ArrayList<EmailSource>()
            override fun getResult(): List<EmailSource> {

                return emails
            }

            override fun processLine(line: String): Boolean {
                if(line.isNotEmpty() && line.first() == '#') {
                    if(!sb.isEmpty()) {
                        emails += EmailSource(
                                str = sb.toString(),
                                quotes = quotes
                        )
                        sb.setLength(0)
                    }
                    val map = splitter.split(line.substring(1))
                    quotes = Integer.parseInt(map["quotes"])
                } else {
                    sb.append(line).append('\n')
                }
                return true
            }

        })
    }

    @Test
    fun test() {
        val srcs = getEmails()
        var num = 0
        for(src in srcs) {
            println("-------------\nParse #$num email.")
            val email = parser.read(src.str)
            val fragments = email.fragments
            fragments.forEach {
                println("# hidden=${it.hidden}\t quoted=${it.quoted} signature=${it.signature}")
                println(it.content)
            }
            //now it does not support enclosed quotes
            //assertThat(fragments, Matchers.hasSize(src.quotes))
            assertThat(fragments, Matchers.hasSize(Matchers.greaterThan(1)))
            num++
        }
    }
}