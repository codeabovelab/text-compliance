package com.codeabovelab.tpc.doc

import com.google.common.collect.ImmutableMap

/**
 */
class DocumentReaders private constructor(
        private val map: Map<String, DocumentReader<*>>
) {

    operator fun get(type: String): DocumentReader<*> {
        val reader = map[type]
        return reader ?: throw NoSuchElementException("Can not find reader for '$type'")
    }

    class Builder {

        private val map = HashMap<String, DocumentReader<*>>()

        operator fun set(reader: DocumentReader<*>, type: String) = apply {
            val old = map.putIfAbsent(type, reader)
            if (old != null && old !== reader) {
                throw IllegalArgumentException("Reader for '$type' is already registered: $old")
            }
        }

        fun build(): DocumentReaders {
            return DocumentReaders(ImmutableMap.copyOf(this.map))
        }
    }
}