package com.codeabovelab.tpc.web

import com.codeabovelab.tpc.core.nn.TextClassifierResult
import com.codeabovelab.tpc.core.processor.Label
import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.core.processor.RuleReport
import com.codeabovelab.tpc.text.TextCoordinatesImpl
import com.codeabovelab.tpc.web.jpa.JpaConfiguration
import org.junit.Test

/**
 */
class JsonSerializeTest {

    private val mapper = JpaConfiguration.createJpaObjectMapper()

    fun <T : Any> test(writed: T): T {
        val str = mapper.writeValueAsString(writed)
        val readed = mapper.readValue<T>(str, writed.javaClass)
        return readed
    }

    @Test
    fun test() {
        val pr =  ProcessorReport.Builder().apply {
            documentId = "testDocId"
            attributes.put("one", 1)
            attributes.put("two", "asdasd")
            attributes.put("three", CustomData())
            val labels = listOf(Label("testLabel", .3))
            report = ProcessorReport.TextReport.Builder().apply {
                textId = "mainText"
                rules.put("oneRule", RuleReport("oneRule", TextClassifierResult(
                        entries = listOf(TextClassifierResult.Entry(
                                coordinates = TextCoordinatesImpl(0, 100), labels = labels)
                        ),
                        labels = labels
                )))
            }.build()
        }.build()
        test(pr)
    }
}

class CustomData {

}
