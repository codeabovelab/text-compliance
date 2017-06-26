package com.codeabovelab.tpc.core.nn.nlp

/**
 */
object UnkDetector {
    val UNK = "UNK"

    fun isUnknown(w: WordData): Boolean {
        if(w.pos.isProperNoun) {
            return true
        }
        var i = w.str.length - 1
        while(i >= 0) {
            val cp = w.str.codePointAt(i)
            if(!Character.isLetter(cp)) {
                return true
            }
            i--
        }
        return false
    }
}
