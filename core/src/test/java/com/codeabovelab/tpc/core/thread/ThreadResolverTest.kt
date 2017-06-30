package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.DocumentsRepositoryImpl
import com.codeabovelab.tpc.doc.MessageDocument
import com.codeabovelab.tpc.doc.MessageDocumentImpl
import com.codeabovelab.tpc.doc.ParentRef
import org.junit.Assert.*
import org.junit.Test
import java.time.ZonedDateTime

/**
 */
class ThreadResolverTest {


    @Test
    fun test() {
        val repo = DocumentsRepositoryImpl()
        repo.register(makeDoc("1", "unexists"))
        repo.register(makeDoc("2", "1", "unexists"))
        repo.register(makeDoc("3", "2", "1"))
        repo.register(makeDoc("4", "3"))
        repo.register(makeDoc("5", "3"))
        val endDoc = makeDoc("6", "5", "4")
        repo.register(endDoc)

        val tr = ThreadResolver(repo = repo)
        val thread = tr.getThread(endDoc)
        assertEquals(listOf("5", "4", "3", "2", "1", "unexists"), thread.documents)
    }

    private fun makeDoc(id: String, vararg parents: String): MessageDocument {
        val mdb = MessageDocumentImpl.Builder()
        mdb.from = "test@test.te"
        mdb.date = ZonedDateTime.now()
        mdb.body(id, "some text")
        for(parent in parents) {
            mdb.references.add(ParentRef(parent))
        }
        return mdb.build()
    }
}