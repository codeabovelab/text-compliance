package com.codeabovelab.tpc.text

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators

/**
 * Something that has text.
 */
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator::class)
interface Textual {

    interface Builder<B : Builder<B>> {
        @Suppress("UNCHECKED_CAST")
        val thiz: B
            get() = this as B

        var id: String?

        fun id(id: String): B {
            this.id = id
            return thiz
        }

        var parent: Textual?

        val childs: MutableList<Textual.Builder<*>>

        fun addChild(child: Textual.Builder<*>): B {
            childs.add(child)
            return thiz
        }

        fun build(): Textual
    }

    /**
     * The id of textual. May be uuid or something else. Must be unique for siblings of its parent.
     * @return non null string
     */
    val id: String

    /**
     * Reference to parent, may be null
     */
    @get:JsonIdentityReference
    val parent: Textual?

    /**
     * Child elements of textual. Do not iterate its manually. Instead use [read] method.
     */
    val childs: List<Textual>
        get() = listOf()

    /**
     * Sequentially invoke consumer on internal text.
     * @param consumer non null value
     */
    fun read(consumer: TextConsumer)
}

typealias TextConsumer = (Textual, Text) -> Unit