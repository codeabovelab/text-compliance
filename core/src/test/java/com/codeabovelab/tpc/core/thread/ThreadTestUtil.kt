package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.*
import java.time.ZonedDateTime

/**
 */
object ThreadTestUtil {
    const val P_ONE = "one@test.te"
    const val P_TWO = "two@test.te"

    fun makeDoc(id: String,
                vararg parents: String,
                callback: MessageDocumentImpl.Builder.() -> Unit = {}): MessageDocument {
        val mdb = MessageDocumentImpl.Builder()
        mdb.from = P_ONE
        mdb.date = ZonedDateTime.now()
        mdb.id = id
        mdb.body("some text")
        for(parent in parents) {
            mdb.references.add(ParentRef(parent))
        }
        mdb.callback()
        return mdb.build()
    }

    fun fillRepo(repo: DocumentsRepositoryImpl) {
        repo.register(makeDoc("1", "unexists") {
            from = P_ONE
            to.add(P_TWO)
        })
        repo.register(makeDoc("2", "1", "unexists") {
            from = P_TWO
            to.add(P_ONE)
        })
        repo.register(makeDoc("3", "2", "1") {
            from = P_ONE
            to.add(P_TWO)
        })
        repo.register(makeDoc("4", "3") {
            from = P_TWO
            to.add(P_ONE)
        })
        repo.register(makeDoc("5", "3"){
            from = P_TWO
            to.add(P_ONE)
        })
    }

}