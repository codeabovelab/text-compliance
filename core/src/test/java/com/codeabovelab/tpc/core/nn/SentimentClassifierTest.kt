package com.codeabovelab.tpc.core.nn

import com.codeabovelab.tpc.core.nn.sentiment.SentimentClassifier
import com.codeabovelab.tpc.core.nn.sentiment.SentimentClassifierResult
import com.codeabovelab.tpc.core.processor.PredicateContext
import com.codeabovelab.tpc.text.TextImpl
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.nio.file.Paths

class SentimentClassifierTest {

    private val log = LoggerFactory.getLogger(this.javaClass)
    var classifier: SentimentClassifier? = null

    @Before
    fun prepare() {
        val modelFile = Paths.get("/home/pronto/Downloads/sentiment/output/sentiment_model_2.zip")
        val wordVectorFile = Paths.get("/home/pronto/Downloads/sentiment/sentiment_vectors.bin")
        classifier = SentimentClassifier(modelFile = modelFile, wordVectorFile = wordVectorFile)
    }

    fun calc(txt: String): SentimentClassifierResult {
        val result = classifier!!.test(PredicateContext.create(), TextImpl(txt))
        log.info("text: {} result: {}", txt, result)
        return result
    }

    @Test
    @Ignore
    fun test() {
        log.info("basic")
        basicTest()
        log.info("positive")
        testPositive()
        log.info("negative")
        testNegative()

    }

    fun basicTest() {
        calc("This is a terrible software, don't waste your money on it. Don't even watch it for free. That's all I have to say.")
        calc("""App works great! Especially when I get a new phone and need to get my stuff back
            I just download Google Drive & my stuff is all there
            """)
        calc("Obviously worthwhile software. Go and use it")
    }

    fun testPositive() {
        calc("""This conference planning meeting went extremely well. Everyone was on the ball and very organized.
                We expect all of the pre-conference tasks to be completed
                a week before the event so everyone can relax a bit before the main event.
                """)
        calc("The demo for this morning was good. Everything worked and all the questions were answered.  Good job team!")
        calc("We tried the new VPN app on my phone and it worked perfectly. There was a little delay but nothing major.")
        calc("""John scheduled a demo walk thru for our VP this morning and he was blown away by the work done by the team.
                Compliment to the team for the hard work so far and let's continue the momentum!
                """)
        calc("The sales meeting with Adobe went well. " +
                "They were satisfied with our requirement gathering document and want us to continue on with the next phase.")
    }

    fun testNegative() {
        calc("""The conference planning meeting did not go well.
                We expected everyone to be present and be ready with their status.
                However, half of the team did not show up and those that did were not prepared for the meeting.
                It looks like we will be cutting it close with all of the meeting tasks.
                We may need to pull some overtime on the weekend to catch up. Let's please get it all together.
                """)

        calc("""The demo this morning was a bit rocky.
                We had to redo it twice for everyone to catch on.
                Also we had intermittent network problems.
                """)
        calc("""The new VPN app didn't work at all.
                After installing it, it kept popping up error messages so we couldn't even enter our credential.
                It still needs a lot of work.
                """)
        calc("""Jeff, can you take a look at the new admin UI page?
                "We did an impromptu demo for our VP and it had errors all over the page. He wasn't pleased.
                "We need to fix it as soon as possible. Thanks!
                """)
        calc("""We had quite a few issue with our SAP meeting this morning.
                The client wanted to see know more details and we didn't have those available.
                We need to be more prepared next time.
                """)
    }

}