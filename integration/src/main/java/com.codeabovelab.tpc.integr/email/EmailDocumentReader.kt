package com.codeabovelab.tpc.integr.email

import com.codeabovelab.tpc.doc.*
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.util.DateTimeUtil

import javax.mail.Session
import javax.mail.internet.MimeMessage
import java.io.InputStream
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.stream.Collectors
import javax.mail.internet.InternetAddress

/**
 *
 */
class EmailDocumentReader:
//we use raw document as return type, because in future it may be changed to more specific type
        DocumentReader<MessageDocumentImpl.Builder> {

    companion object {
        const val F_SUBJECT = "subject"
    }

    private val emailParser = EmailParser()

    override val info = DocumentReader.Info(
            binary = false,
            type = "email"
    )

    override fun read(id: String?, istr: InputStream): MessageDocumentImpl.Builder {
        var session: Session? = null
        val msg = MimeMessage(session, istr)
        val db = MessageDocumentImpl.Builder()
        db.id = id ?: msg.messageID
        db.body = TextImpl(getContent(msg))

        addField(db, F_SUBJECT, msg.subject)
        db.from = extractSender(msg)
        // parser produce errors like ' javax.mail.internet.AddressException: Domain contains illegal character in string'
        // on any address with _ and may other symbols
        msg.allRecipients?.forEach { db.to.add(toString(it)!!) }
        db.date = ZonedDateTime.ofInstant(msg.sentDate.toInstant(), ZoneOffset.UTC)
        extractReferences(msg, db.references)
        return db
    }

    private fun  extractReferences(msg: MimeMessage, refs: MutableList<Ref>) {
        val refsHeader = msg.getHeader("References", null)
        var ref: ParentRef? = null
        fun updateRef(addr: InternetAddress) {
            ref = ParentRef(addr.address, if(ref != null) listOf(ref!!) else null)
        }
        if(refsHeader != null) {
            val refsAddr = InternetAddress.parseHeader(refsHeader, false)
            for(addr in refsAddr) {
                updateRef(addr)
            }
        }
        val inReplyToHeader = msg.getHeader("In-Reply-To", null)
        if(inReplyToHeader != null) {
            val inReplyAddr = InternetAddress.parseHeader(inReplyToHeader, false)
            if(inReplyAddr.isNotEmpty()) {
                val addr = inReplyAddr[0]
                if(ref == null || ref!!.id != addr.address) {
                    updateRef(addr)
                }
            }
        }
        if(ref != null) {
            refs.add(ref!!)
        }
    }

    private fun extractSender(msg: MimeMessage): String? {
        val from = msg.from
        val sender = msg.sender
        if((from == null || from.isEmpty()) && sender == null) {
            return null
        }
        //note that MimeMessage may return non null 'from' even if it header is null,
        // because it use sender header too
        return if(sender == null) {
            toString(from[0])
        } else {
            toString(sender)
        }
    }

    private fun addField(db: MessageDocumentImpl.Builder, name: String, value: Any?) {
        val str = toString(value)
        db.addChild(DocumentFieldImpl.Builder().id(name).data(str))
    }

    private fun toString(value: Any?): String? {
        if(value == null) {
            return null
        }
        val str: String?
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
        } else if(value is InternetAddress) {
            str = value.address
        } else {
            str = value.toString()
        }
        return str
    }

    private fun getContent(msg: MimeMessage): String {
        //here we must extract text from html
        val string = msg.content as String
        if(HtmlParser.isOur(string)) {
            return HtmlParser.toText(string)
        }
        return emailParser.read(string).visibleText()
    }
}