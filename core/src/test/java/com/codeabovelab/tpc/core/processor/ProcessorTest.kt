package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.core.thread.ThreadResolver
import com.codeabovelab.tpc.core.thread.ThreadTestUtil
import com.codeabovelab.tpc.doc.DocumentImpl
import com.codeabovelab.tpc.doc.DocumentsRepositoryImpl
import org.junit.Test
import kotlin.reflect.full.cast
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 */
class ProcessorTest {

    @Test
    fun simpleTest() {
        val processor = Processor()
        processor.addRule(Rule("simple", 0F, RegexPredicate("MARK"), SetAttributeAction("found", "mark")))
        val firstReport = processor.process(DocumentImpl.Builder()
          .id("first")
          .body("some text MARK \n ant some other MARK text \n MARK")
          .build())
        System.out.println(firstReport)
    }

    @Test
    fun threadParticipantsTread() {
        val repo = DocumentsRepositoryImpl()
        ThreadTestUtil.fillRepo(repo)
        val added = "unknown@test"
        val removed = ThreadTestUtil.P_ONE
        val endDoc = ThreadTestUtil.makeDoc("6", "5", "4") {
            this.from = ThreadTestUtil.P_TWO
            this.to.addAll(listOf(added))
        }
        repo.register(endDoc)

        val processor = Processor(threadResolver = ThreadResolver(repo))
        processor.addRule(Rule("threadParticipant", 0F, ParticipantPredicate(), RuleAction.NOP))

        val report = processor.process(endDoc)

        val predicateRes = report.findPredicateResult<ParticipantPredicate.Result>()
        assertNotNull(predicateRes)
        println("added: ${predicateRes!!.added}")
        println("removed: ${predicateRes.removed}")
        assertEquals(listOf(added), predicateRes.added)
        assertEquals(listOf(removed), predicateRes.removed)
    }


    inline fun <reified T : PredicateResult<*>> ProcessorReport.findPredicateResult(): T? {
        val type = T::class
        val res = report.rules.values.find {
            type.isInstance(it.result)
        }
        return if(res != null) type.cast(res.result) else null
    }
}