package com.codeabovelab.tpc.core.nn.nlp

import org.junit.Assert.*
import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader

/**
 */
class NlpParserTest {
    @Test
    fun test() {
        val p = NlpParser()
        val data = """A|p=DT|l=a team|p=NN member|p=NN will|p=MD contact|p=VB you|p=PRP tomorrow|p=NN with|p=IN a|p=DT detailed|p=JJ explanation|p=NN of|p=IN the|p=DT product|p=NN that|p=WDT fits|p=VBZ|l=fit yo
Thanks|p=NNS|l=thanks again|p=RB for|p=IN your|p=PRP$ inquiry|p=NN .
Thank|p=VBP|l=thank you|p=PRP for|p=IN your|p=PRP$ order|p=NN of|p=IN 25|p=CD|l=0 DVDs|p=NNS|l=dvd .
We|p=PRP|l=we will|p=MD send|p=VB them|p=PRP within|p=IN the|p=DT next|p=JJ 3|p=CD|l=0 days|p=NNS|l=day .
Is|p=VBZ|l=be your|p=PRP$ computer|p=NN a|p=DT Mac|p=NNP|l=mac or|p=CC PC|p=NN|l=pc ?"""
        val res = p.parse(BufferedReader(StringReader(data)))
        res.forEach {
            println(it.str)
            println(it.offset)
            println(it.words)
        }
    }
}