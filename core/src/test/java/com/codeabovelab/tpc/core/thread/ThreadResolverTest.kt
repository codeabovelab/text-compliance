package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.DocumentsRepositoryImpl
import org.junit.Assert.*
import org.junit.Test

/**
 */
class ThreadResolverTest {


    @Test
    fun test() {
        val repo = DocumentsRepositoryImpl()
        ThreadTestUtil.fillRepo(repo)
        val endDoc = ThreadTestUtil.makeDoc("6", "5", "4")
        repo.register(endDoc)

        val tr = ThreadResolver(repo = repo)
        val thread = tr.getThread(endDoc)
        assertEquals(listOf("unexists", "1", "2", "3", "4", "5", "6"), thread.documents)
    }

}