package com.codeabovelab.tpc.integr.email;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.doc.DocumentFieldImpl;
import com.codeabovelab.tpc.doc.DocumentImpl;
import com.codeabovelab.tpc.util.DateTimeUtil;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public final class EmailToDocument {

    public static final String F_SUBJECT = "subject";
    public static final String F_SENDER = "sender";
    public static final String F_FROM = "from";
    public static final String F_RECIPIENTS = "recipients";
    public static final String F_SENT_DATE = "sentDate";

    public Document apply(InputStream is) throws Exception {
        Session session = null;
        MimeMessage msg = new MimeMessage(session, is);
        DocumentImpl.Builder db = DocumentImpl.builder()
          .id(msg.getMessageID())
          .body(getContent(msg));
        addField(db, F_SUBJECT, msg.getSubject());
        addField(db, F_SENDER, msg.getSender());
        addField(db, F_FROM, msg.getFrom());
        // parser produce errors like ' javax.mail.internet.AddressException: Domain contains illegal character in string'
        // on any address with _ and may other symbols
        addField(db, F_RECIPIENTS, msg.getAllRecipients());
        addField(db, F_SENT_DATE, msg.getSentDate());
        return db.build();
    }

    private void addField(DocumentImpl.Builder db, String name, Object value) throws Exception {
        String str = toString(value);
        db.addField(DocumentFieldImpl.builder().name(name).data(str));
    }

    private String toString(Object value) {
        if(value == null) {
            return null;
        }
        String str;
        if(value.getClass().isArray()) {
            // we not expect arrays of primitive here
            Object[] arr = (Object[]) value;
            int len = arr.length;
            if(len == 0) {
                str = null;
            } else if(len == 1) {
                str = toString(arr[0]);
            } else {
                str = Arrays.stream(arr).map(this::toString).collect(Collectors.joining(", "));
            }
        } else if(value instanceof Date) {
            str = DateTimeUtil.toUTCString((Date) value);
        } else {
            str = value.toString();
        }
        return str;
    }

    private String getContent(MimeMessage msg) throws Exception {
        //here we must extract text from html
        return (String) msg.getContent();
    }
}