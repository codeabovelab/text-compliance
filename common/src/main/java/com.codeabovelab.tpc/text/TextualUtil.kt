package com.codeabovelab.tpc.text;

/**
 */
object TextualUtil {
    /**
     * Read textual to string. It may doing it in non efficient way, therefore you can use it only for
     * debugging and testing.
     * @param textual textual or null
     * @return string or null
     */
    @JvmStatic fun read(textual: Textual?): String? {
        if(textual == null) {
            return null
        }
        val sb = StringBuilder()
        textual.read { _, text -> sb.append(text.data) }
        return sb.toString()
    }
}
