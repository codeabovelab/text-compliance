package com.codeabovelab.tpc.tool.learn.sentiment

import org.junit.Ignore
import org.junit.Test
import java.nio.file.Paths

class PrepareReviewDataTest {
    @Test
    @Ignore
    fun parse() {
        val prepare = PrepareReviewData(dataDirectory = Paths.get("/home/pronto/Downloads/sentiment/amazon/"),
                outputDirectory = Paths.get("/home/pronto/Downloads/sentiment/output-amazon/"))
        prepare.parse()
    }

}