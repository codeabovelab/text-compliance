package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.*
import com.google.common.collect.ImmutableList
import java.util.*

/**
 */
class ThreadResolver(
        private val repo: DocumentsRepository
) {

    fun getThread(doc: MessageDocument): MessagesThread {
        val ids = ArrayList<String>()
        val loaded = HashMap<String, Document?>()
        ids.add(doc.id)
        //put self for cases with cyclic references
        loaded.put(doc.id, doc)
        val parents = LinkedList<List<Ref>>()
        parents.add(doc.references)
        while(parents.isNotEmpty()) {
            loadParents(loaded, ids, parents)
        }
        ids.reverse()
        return LazyMessagesThread(ImmutableList.copyOf(ids), loaded)
    }

    private fun loadParents(
            cache: MutableMap<String, Document?>,
            ids: MutableList<String>,
            parents: Deque<List<Ref>>
    ) {
        val refs = parents.removeFirst()
        for (ref in refs) {
            if (ref !is ParentRef || cache.containsKey(ref.id)) {
                continue
            }
            ids.add(ref.id)
            var parentRefs: List<Ref>? = ref.parent
            if(parentRefs == null) {
                val parentDoc = repo[ref.id]
                // we always put doc even if it null, fo prevent multiple loading
                cache.put(ref.id, parentDoc)
                if (parentDoc != null && parentDoc is MessageDocument) {
                    parentRefs = parentDoc.references
                }
            }
            if(parentRefs != null && !parentRefs.isEmpty()) {
                parents.addLast(parentRefs)
            }
        }
    }

    inner class LazyMessagesThread(
            override val documents: List<String>,
            private val loaded: Map<String, Document?>
    ): MessagesThread {

        override fun getDocument(id: String): Document? {
            // do not collapse it into one line!
            var doc = loaded[id]
            if(doc == null) {
                doc = repo[id]
            }
            return doc
        }

    }
}