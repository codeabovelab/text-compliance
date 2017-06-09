package com.codeabovelab.tpc.core.nn;

import com.codeabovelab.tpc.doc.Document;
import com.codeabovelab.tpc.doc.DocumentImpl;
import com.codeabovelab.tpc.integr.email.EmailDocumentReader;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 */
@Slf4j
class DocIter {

    private static final Pattern SKIP_HTML = Pattern.compile("<[\\S][^>]*>", Pattern.DOTALL);
    private final EmailDocumentReader etd = new EmailDocumentReader();
    private final String dir;
    private Document doc;
    private Iterator<Path> fileIter;
    private int counter;

    public DocIter(String dir) {
        this.dir = dir;
        reset();
    }

    private void processEmail(Path path) {
        try (InputStream is = new FileInputStream(path.toFile())) {
            this.doc = etd.apply(is);
        } catch (Exception e) {
            log.error("Can not read: {} due to error: {}", path, e.toString());
        }
    }

    public String currentLabel() {
        return doc.getId();
    }

    public String nextDocument() {
        counter++;
        Path path = fileIter.next();
        log.info("Choose {}-th mail at path {}", counter, path);
        processEmail(path);
        String str = ((DocumentImpl) doc).getBody().getData();
        // workaround to skip html tags
        str = SKIP_HTML.matcher(str).replaceAll("");
        Objects.requireNonNull(str);
        return str;
    }


    public boolean hasNext() {
        return fileIter.hasNext();
    }

    public void reset() {
        try {
            counter = 0;
            Stream<Path> stream = Files.walk(Paths.get(dir))
              .filter(path -> path.toString().endsWith(".eml"))
              // without limit we get 'java.lang.OutOfMemoryError: GC overhead limit exceeded'
              .limit(1000L);
            this.fileIter = stream.iterator();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public int getCounter() {
        return counter;
    }
}
