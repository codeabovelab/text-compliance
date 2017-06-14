package com.codeabovelab.tpc.integr.email

/**
 * Simple variant ot tool which extract text from html docs.
 */
internal object HtmlParser {

    private val TAG = Regex("<[^>]+>")

    fun isOur(string: String): Boolean {
        val tagStart = string.indexOf("<")
        if(tagStart < 0) {
            return false
        }
        for(i in (0 until tagStart)) {
            if(!string[i].isWhitespace()) {
                return false
            }
        }
        return true
    }

    fun toText(string: String): String {
        //in future we must break string into instances of Text chunks, with save it coordinates,
        // but now we use most simply way
        return TAG.replace(string, "")
    }

}