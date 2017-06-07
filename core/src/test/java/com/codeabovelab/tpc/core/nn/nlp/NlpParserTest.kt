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
        val data = """Thank|p=VBP|l=thank you|p=PRP for|p=IN inquiring|p=VBG|l=inquire about|p=IN our|p=PRP$ new|p=JJ email|p=NN marketing|p=NN enterprise|p=NN application|p=NN .
A|p=DT|l=a team|p=NN member|p=NN will|p=MD contact|p=VB you|p=PRP tomorrow|p=NN with|p=IN a|p=DT detailed|p=JJ explanation|p=NN of|p=IN the|p=DT product|p=NN that|p=WDT fits|p=VBZ|l=fit yo
Thanks|p=NNS|l=thanks again|p=RB for|p=IN your|p=PRP$ inquiry|p=NN .
Thank|p=VBP|l=thank you|p=PRP for|p=IN your|p=PRP$ order|p=NN of|p=IN 25|p=CD|l=0 DVDs|p=NNS|l=dvd .
We|p=PRP|l=we will|p=MD send|p=VB them|p=PRP within|p=IN the|p=DT next|p=JJ 3|p=CD|l=0 days|p=NNS|l=day .
Before|p=IN|l=before we|p=PRP send|p=VBP them|p=PRP however|p=RB , we|p=PRP need|p=VBP to|p=TO know|p=VB the|p=DT type|p=NN of|p=IN package|p=NN you|p=PRP prefer|p=VBP .
Kindly|p=RB|l=kindly visit|p=VB your|p=PRP$ order|p=NN page|p=NN and|p=CC select|p=VB your|p=PRP$ preference|p=NN .
If|p=IN|l=if you|p=PRP have|p=VBP any|p=DT question|p=NN , call|p=VB us|p=PRP at|p=IN .
You|p=PRP|l=you will|p=MD be|p=VB promptly|p=RB attended|p=VBN|l=attend to|p=IN by|p=IN the|p=DT customer|p=NN service|p=NN team|p=NN .
Thanks|p=NNS|l=thanks again|p=RB for|p=IN your|p=PRP$ order|p=NN .
We|p=PRP|l=we look|p=VBP forward|p=RB to|p=IN your|p=PRP$ final|p=JJ instructions|p=NNS|l=instruction .
Thank|p=VBP|l=thank you|p=PRP for|p=IN inquiring|p=VBG|l=inquire about|p=IN the|p=DT email|p=NN software|p=NN advertised|p=VBN|l=advertise on|p=IN my|p=PRP$ blog|p=NN .
Each|p=DT|l=each of|p=IN the|p=DT listed|p=VBN|l=list software|p=NN functions|p=NNS|l=function uniquely|p=RB on|p=IN different|p=JJ platforms|p=NNS|l=platform .
Before|p=IN|l=before I|p=PRP recommend|p=VBP a|p=DT particular|p=JJ one|p=NN|l=#crd# , I|p=PRP would|p=MD like|p=VB to|p=TO know|p=VB a|p=DT bit|p=NN more|p=RBR about|p=IN you|p=PRP and|p=
Are|p=VBP|l=be you|p=PRP self|p=NN - employed|p=VBN|l=employ , manager|p=NN or|p=CC a|p=DT business|p=NN owner|p=NN ?
Will|p=MD|l=will you|p=PRP be|p=VB using|p=VBG|l=use the|p=DT software|p=NN on|p=IN a|p=DT mobile|p=JJ device|p=NN or|p=CC computer|p=NN ?
Is|p=VBZ|l=be your|p=PRP$ computer|p=NN a|p=DT Mac|p=NNP|l=mac or|p=CC PC|p=NN|l=pc ?"""
        val res = p.parse(BufferedReader(StringReader(data)))
        res.forEach {
            println(it.str)
            println(it.offset)
            println(it.words)
        }
    }
}