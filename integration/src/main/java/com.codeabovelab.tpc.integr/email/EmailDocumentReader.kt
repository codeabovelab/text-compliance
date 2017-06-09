package com.codeabovelab.tpc.integr.email;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.doc.DocumentFieldImpl;
import com.codeabovelab.tpc.doc.DocumentImpl;
import com.codeabovelab.tpc.doc.DocumentReader
import com.codeabovelab.tpc.util.DateTimeUtil;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

class EmailDocumentReader: DocumentReader {

    companion object {
        const val F_SUBJECT = "subject"
        const val F_SENDER = "sender"
        const val F_FROM = "from"
        const val F_RECIPIENTS = "recipients"
        const val F_SENT_DATE = "sentDate"
    }

    private val emailParser = EmailParser()

    override fun read(istr: InputStream): Document {
        var session: Session? = null
        val msg = MimeMessage(session, istr)
        val db = DocumentImpl.builder()
          .id(msg.messageID)
          .body(getContent(msg))
        addField(db, F_SUBJECT, msg.subject)
        addField(db, F_SENDER, msg.sender)
        addField(db, F_FROM, msg.from)
        // parser produce errors like ' javax.mail.internet.AddressException: Domain contains illegal character in string'
        // on any address with _ and may other symbols
        addField(db, F_RECIPIENTS, msg.allRecipients)
        addField(db, F_SENT_DATE, msg.sentDate)
        return db.build()
    }

    private fun addField(db: DocumentImpl.Builder, name: String, value: Any?) {
        val str = toString(value)
        db.addField(DocumentFieldImpl.builder().name(name).data(str))
    }

    private fun toString(value: Any?): String? {
        if(value == null) {
            return null
        }
        var str: String?
        if(value.javaClass.isArray) {
            // we not expect arrays of primitive here
            val arr = value as Array<*>
            val len = arr.size
            if(len == 0) {
                str = null
            } else if(len == 1) {
                str = toString(arr[0])
            } else {
                str = Arrays.stream(arr).map(this::toString).collect(Collectors.joining(", "))
            }
        } else if(value is Date) {
            str = DateTimeUtil.toUTCString(value)
        } else {
            str = value.toString()
        }
        return str
    }

    private fun getContent(msg: MimeMessage): String {
        //here we must extract text from html
        val string = msg.content as String
        return emailParser.read(string).visibleText()
    }
}