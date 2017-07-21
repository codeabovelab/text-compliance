package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.core.processor.ProcessorReport
import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.web.jpa.DocEntity
import com.codeabovelab.tpc.web.jpa.DocsRepository
import com.codeabovelab.tpc.util.JsonBlobs
import com.codeabovelab.tpc.web.docproc.DocProcessor
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 */
@RequestMapping("/doc")
@Transactional(propagation = Propagation.REQUIRED)
@RestController
open class UiDocumentController(
        private var repository: DocsRepository,
        private var readers: DocumentReaders
) {

    @RequestMapping("/list", method = arrayOf(RequestMethod.GET))
    fun list(): List<String>? {
        return repository.findAll().map { it.documentId }
    }

    @RequestMapping("/get", method = arrayOf(RequestMethod.GET))
    fun get(id: String): UiDoc? {
        val docEntity = repository.findByDocumentId(id)
        return docEntity.toUi()
    }

    @RequestMapping("/set", method = arrayOf(RequestMethod.POST))
    fun set(@RequestBody ui: UiDoc) {
        val docEntity = repository.findByDocumentId(ui.documentId!!) ?: DocEntity()
        val entity = ui.toEntity(docEntity, readers)
        repository.save(entity)
    }

    @RequestMapping("/delete", method = arrayOf(RequestMethod.POST))
    fun delete(id : String) {
        repository.deleteByDocumentId(id)
    }
}

fun DocEntity?.toUi(): UiDoc? {
    if(this == null) {
        return null
    }
    val ui = UiDoc()
    ui.type = type
    ui.documentId = documentId
    ui.data = JsonBlobs.toString(data, binary)
    return ui
}

class UiDoc {
    var type: String? = null
    var documentId: String? = null
    var data: String? = null

    fun toEntity(entity: DocEntity, readers: DocumentReaders): DocEntity {
        entity.type = this.type!!
        entity.documentId = this.documentId!!
        entity.binary = readers.isBinary(entity.type)
        entity.data = JsonBlobs.fromString(this.data!!, entity.binary)
        return entity
    }
}