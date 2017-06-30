package com.codeabovelab.tpc.doc

/**
 * Simple implementation of document repository.
 * Usually usable for testing and debugging.
 */
class DocumentsRepositoryImpl : DocumentsRepository {

    val documents: MutableMap<String, Document> = HashMap()

    override fun get(id: String): Document? = documents[id]

    fun register(doc: Document): Unit {
        val old = documents.putIfAbsent(doc.id, doc)
        if(old != null && old !== doc) {
            throw IllegalArgumentException("Repository already has document with id: ${doc.id}")
        }
    }
}