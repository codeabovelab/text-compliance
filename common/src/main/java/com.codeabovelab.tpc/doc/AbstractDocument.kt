package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.Text
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.text.Textual
import com.google.common.collect.ImmutableList
import java.util.ArrayList

/**
 */
abstract class AbstractDocument(b: AbstractDocument.Builder<*>) : Document {

    abstract class Builder<B: AbstractDocument.Builder<B>>: Document.Builder<B> {
        override var body: Text? = null
        override var id: String? = null
        override var attributes: MutableMap<String, Any?> = HashMap()
        override var parent: Textual? = null
        override val childs: MutableList<Textual.Builder<*>> = ArrayList()

        fun body(body: String): B {
            this.body = TextImpl(body)
            return thiz
        }

        override abstract fun build(): Document

    }

    override val id: String = b.id!!
    val body: Text = b.body!!
    override val parent: Textual? = b.parent
    override final val childs: List<Textual>

    init {
        val fb = ImmutableList.builder<Textual>()
        b.childs.forEach {
            it.parent = this
            fb.add(it.build())
        }
        this.childs = fb.build()
    }
}