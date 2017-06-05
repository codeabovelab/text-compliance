package com.codeabovelab.tpc.integr.email

import com.google.common.io.Resources
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.io.IOException


internal class EmailParserTest {

    val parser: EmailParser = EmailParser()

    private fun getEmail(name: String): String {
        try {
            val url = Resources.getResource(this.javaClass, String.format("/emails/%s.txt", name))
            return Resources.toString(url, Charsets.UTF_8)
        } catch (e: IOException) {
            throw RuntimeException(String.format("No email file found: %s", name), e)
        }

    }

    @Test
    fun test() {
        val email = parser.read(getEmail("email_1"))
        val fragments = email.fragments
        println("Hidden: \n" + email.hiddenText())
        println("Visible: \n" + email.visibleText())
        assertThat(fragments, Matchers.hasSize(2))
        assertThat(fragments[0].content, containsString("Awesome"))
        assertThat(fragments[0].hidden, equalTo(false))
        assertThat(fragments[1].content, containsString("On"))
        assertThat(fragments[1].hidden, equalTo(true))
        assertThat(fragments[1].content, containsString("Loader"))
    }

    @Test
    fun enronTest() {
        val email = parser.read(getEmail("email"))
        val fragments = email.fragments
        println("Hidden: \n" + email.hiddenText())
        println("Visible: \n" + email.visibleText())
        assertThat(fragments, Matchers.hasSize(2))
        assertThat(fragments[0].content, containsString("Phillip"))
        assertThat(fragments[0].hidden, equalTo(false))
        assertThat(fragments[1].content, containsString("As discussed"))
        assertThat(fragments[1].quoted, equalTo(true))
        assertThat(fragments[1].hidden, equalTo(true))
    }

}