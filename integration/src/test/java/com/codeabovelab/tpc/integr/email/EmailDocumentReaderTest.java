package com.codeabovelab.tpc.integr.email;

import com.codeabovelab.tpc.doc.DocumentField;
import com.codeabovelab.tpc.doc.DocumentImpl;
import com.codeabovelab.tpc.text.TextualUtil;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

/**
 */
public class EmailDocumentReaderTest {

    private static final List<String> FIELDS = ImmutableList.of(
      EmailDocumentReader.F_FROM,
      EmailDocumentReader.F_RECIPIENTS,
      EmailDocumentReader.F_SENDER,
      EmailDocumentReader.F_SUBJECT,
      EmailDocumentReader.F_SENT_DATE
    );

    @Test
    public void test() throws Exception {
        String msg = "Received: from mxfront9m.mail.yandex.net ([127.0.0.1])\n" +
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
          "Thanks!\n";
        EmailDocumentReader etd = new EmailDocumentReader();
        DocumentImpl doc = (DocumentImpl) etd.read(new ByteArrayInputStream(msg.getBytes())).build();
        System.out.println(doc);
        assertNotNull(doc.getBody().getData());
        Map<String, DocumentField> fields = doc.getFields().stream().collect(Collectors.toMap(DocumentField::getName, Function.identity()));
        for(String name: FIELDS) {
            System.out.println("Test field: " + name);
            DocumentField df = fields.get(name);
            assertNotNull(df);
            System.out.println("\t" + TextualUtil.read(df));
        }
    }

}