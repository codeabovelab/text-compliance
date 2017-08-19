package com.codeabovelab.tpc.doc

import com.codeabovelab.tpc.text.TextConsumer
import com.codeabovelab.tpc.text.TextImpl
import com.codeabovelab.tpc.text.Textual
import com.codeabovelab.tpc.text.TextualUtil

/**
 */
class DocumentFieldImpl private constructor(b: DocumentFieldImpl.Builder) : DocumentField {

    class Builder : DocumentField.Builder<Builder> {
        override var id: String? = null
        override var parent: Textual? = null
        override var childs = ArrayList<Textual.Builder<*>>()
        var data: String? = null

        fun data(data: String?): Builder {
            this.data = data
            return this
        }

        override fun build(): DocumentFieldImpl {
            return DocumentFieldImpl(this)
        }
    }

    override val id: String = b.id!!
    override val parent: Textual? = b.parent!!
    val data: TextImpl
    override val childs: List<Textual> = TextualUtil.buildChilds(this, b)

    init {
        this.data = TextImpl(b.data.orEmpty())
    }

    override fun read(consumer: TextConsumer) {
        consumer(this, data)
    }
}
