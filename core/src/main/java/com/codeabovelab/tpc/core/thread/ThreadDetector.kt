package com.codeabovelab.tpc.core.thread

import com.codeabovelab.tpc.doc.DocumentsRepository
import com.codeabovelab.tpc.doc.MessageDocument

/**
 */
class ThreadDetector(
        private val repo: DocumentsRepository
) {

    fun getThread(doc: MessageDocument): MessagesThread {
        return MessagesThread.NONE
    }
}