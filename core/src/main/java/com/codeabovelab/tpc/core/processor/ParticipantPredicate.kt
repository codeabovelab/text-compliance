package com.codeabovelab.tpc.core.processor

import com.codeabovelab.tpc.doc.MessageDocument
import com.codeabovelab.tpc.text.Text
import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.*

/**
 * Analyze thread and current doc for detect participant adding/removing.
 */
@JsonTypeName("ParticipantPredicate")
class ParticipantPredicate : RulePredicate<ParticipantPredicate.Result> {

    val emptyResult = Result(added = listOf(), removed = listOf())

    override fun test(pc: PredicateContext, text: Text): Result {
        val docs = pc.thread.documents
        if(docs.size < 2 /*current doc and it parent*/ ) {
            return emptyResult
        }
        val curr = pc.document // last doc of thread same as current
        val prev = pc.thread.getDocument(docs[docs.size - 2])
        if(curr !is MessageDocument || prev !is MessageDocument) {
            return emptyResult
        }
        val addedSet = HashSet<String>()
        val prevParticipants = participants(prev)
        addedSet.add(curr.from)
        addedSet.addAll(curr.to)
        // remove common elements from both collections
        addedSet.removeIf { prevParticipants.remove(it) }
        val added = ArrayList(addedSet)
        added.sort()
        val removed = ArrayList<String>(prevParticipants)
        removed.sort()
        return Result(
                added = Collections.unmodifiableList(added),
                removed = Collections.unmodifiableList(removed)
        )
    }

    private fun participants(doc: MessageDocument): MutableSet<String> {
        val set = HashSet<String>()
        set.addAll(doc.to)
        set.add(doc.from)
        return set
    }

    /**
     * Result contains analysis of participation in message thread. Note that
     * it does not analyze text, therefore it do not provide entries.
     */
    class Result(
            val added: List<String>,
            val removed: List<String>
    ) : PredicateResult<PredicateResult.Entry>(listOf()) {

        override fun isEmpty(): Boolean {
            return added.isEmpty() && removed.isEmpty()
        }
    }
}