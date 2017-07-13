package com.codeabovelab.tpc.doc

import com.google.common.collect.ImmutableMap

/**
 */
class DocumentReaders private constructor(
        private val map: Map<String, DocumentReader<*>>
) {

    operator fun get(type: String): DocumentReader<*>? {
        return map[type]
    }

    /**
     * return true if type is binary or unknown
     */
    fun isBinary(type: String): Boolean {
        return this.get(type)?.info?.binary ?: true
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