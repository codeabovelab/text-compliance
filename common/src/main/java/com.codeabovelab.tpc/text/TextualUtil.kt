package com.codeabovelab.tpc.text;

import com.google.common.collect.ImmutableList

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

    /**
     * Do not make it method as member of any [Textual] class, because it must not be part of public API.
     */
    fun buildChilds(parent: Textual, builder: Textual.Builder<*>): List<Textual> {
        val fb = ImmutableList.builder<Textual>()
        builder.childs.forEach {
            it.parent = parent
            fb.add(it.build())
        }
        return fb.build()
    }
}
