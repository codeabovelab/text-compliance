package com.codeabovelab.tpc.integr.email

import com.codeabovelab.tpc.doc.DocumentField
import com.codeabovelab.tpc.doc.MessageDocumentImpl
import com.codeabovelab.tpc.doc.ParentRef
import com.codeabovelab.tpc.text.TextualUtil
import org.junit.Test

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 */
class EmailDocumentReaderTest {

    @Test
    fun test() {
        val msg = "Received: from mxfront9m.mail.yandex.net ([127.0.0.1])\n" +
                "\tby mxfront9m.mail.yandex.net with LMTP id jNGsscI3\n" +
                "\tfor <test@ya.ru>; Thu, 18 May 2017 16:32:26 +0300\n" +
                "Received: from github-smtp2-ext2.iad.github.net (github-smtp2-ext2.iad.github.net [192.30.252.193])\n" +
                "\tby mxfront9m.mail.yandex.net (nwsmtp/Yandex) with ESMTPS id NxxtuFBhbF-WPPCWKJ7;\n" +
                "\tThu, 18 May 2017 16:32:25 +0300\n" +
                "\t(using TLSv1.2 with cipher ECDHE-RSA-AES128-GCM-SHA256 (128/128 bits))\n" +
                "\t(Client certificate not present)\n" +
                "X-Yandex-Front: mxfront9m.mail.yandex.net\n" +
                "X-Yandex-TimeMark: 1495114345\n" +
                "Authentication-Results: mxfront9m.mail.yandex.net; spf=pass (mxfront9m.mail.yandex.net: domain of github.com designates 192.30.252.193 as permitted sender, rule=[ip4:192.30.252.0/22]) smtp.mail=noreply@github.com; dkim=pass header.i=@github.com\n" +
                "X-Yandex-Spam: 2\n" +
                "Date: Thu, 18 May 2017 06:32:24 -0700\n" +
                "DKIM-Signature: v=1; a=rsa-sha256; c=relaxed/relaxed; d=github.com;\n" +
                "\ts=pf2014; t=1495114344;\n" +
                "\tbh=8UdgKznAQNbYFxhn6CJWFVv4bCZ4hTdTUz6jDhufFI0=;\n" +
                "\th=From:To:Subject:From;\n" +
                "\tb=kWNtbVbkCSxWcFoHriHl3xcyZBjuPEaABSxZhxZWYuqpESG2vWiQyO0TAOZUbpFx0\n" +
                "\t lS0uGM6k0/FLHdgYn7Hga1zF7LXOiY3xGTVYeXIzvDyAFbon4Kx86+wjw2L4j8tFnP\n" +
                "\t NGAe2nZK13PVwkQ6xWxYFpT4iu8WTy5kS4xTdvC0=\n" +
                "From: GitHub <noreply@github.com>\n" +
                "To: test@ya.ru\n" +
                "Message-ID: <591da2685362b_45593f84217a9c2c388c0@github-lowworker7-cp1-prd.iad.github.net.mail>\n" +
                "Subject: [GitHub] Subscribed to codeabovelab/haven-platform notifications\n" +
                "Mime-Version: 1.0\n" +
                "Content-Type: text/plain;\n" +
                " charset=UTF-8\n" +
                "Content-Transfer-Encoding: quoted-printable\n" +
                "In-Reply-To: <23@web6j.yandex.ru>\n" +
                "References: <asomeotherid@host> <Sj@OfsysSMTP.hq2.rep> <23@web6j.yandex.ru>"
        "X-Auto-Response-Suppress: All\n" +
                "Return-Path: noreply@github.com\n" +
                "X-Yandex-Forward: f7fef8356ed7d07132f32edacecf2b50\n" +
                "\n" +
                "\n" +
                "Hey there, we=E2=80=99re just writing to let you know that you=E2=80=99ve=\n" +
                " been automatically subscribed to a repository on GitHub.\n" +
                "You=E2=80=99ll receive notifications for all issues, pull requests, and c=\n" +
                "omments that happen inside the repository. If you would like to stop watc=\n" +
                "hing this repository, you can manage your settings here:\n" +
                "You were automatically subscribed because you=E2=80=99ve been given acces=\n" +
                "s to the repository.\n" +
                "\n" +
                "Thanks!\n"
        val etd = EmailDocumentReader()
        val builder = etd.read(null, ByteArrayInputStream(msg.toByteArray(StandardCharsets.UTF_8)))
        val testAttrKey = "test_attribute"
        builder.attributes.put(testAttrKey, 1)
        val doc = builder.build() as MessageDocumentImpl
        System.out.println(doc)
        assertNotNull(doc.body.data)
        val map = HashMap<String, DocumentField>()
        doc.read { textual, _ ->
            if (textual is DocumentField) {
                map.put(textual.id, textual)
            }
        }

        assertEquals(
                listOf(ParentRef("23@web6j.yandex.ru", listOf(ParentRef("Sj@OfsysSMTP.hq2.rep", listOf(ParentRef("asomeotherid@host")))))),
                doc.references
        )

        run {
            val fieldNames = mutableSetOf(
                    EmailDocumentReader.F_SUBJECT
            )
            map.forEach { k, df ->
                println("Test field: " + k)
                fieldNames.remove(k)
                assertNotNull(df)
                val text = TextualUtil.read(df)
                println("\t" + text)
            }
            assertTrue(fieldNames.isEmpty(), "It must be an empty: $fieldNames")
        }

        run {
            val attrNames = mutableSetOf(
                    "from",
                    "to",
                    "date",
                    testAttrKey
            )
            doc.attributes.forEach { k, v ->
                println("Test attribute: $k")
                attrNames.remove(k)
                println("\t $v")
            }
            assertTrue(attrNames.isEmpty(), "It must be an empty: $attrNames")
        }
    }

}