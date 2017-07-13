package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.web.jpa.DocEntity
import com.codeabovelab.tpc.web.jpa.DocsRepository
import com.codeabovelab.tpc.util.JsonBlobs
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 */
@RequestMapping("/doc")
@RestController
class UiDocuments(
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
        return if(docEntity != null) {
            UiDoc().toUi(docEntity)
        } else {
            null
        }
    }

    @RequestMapping("/set", method = arrayOf(RequestMethod.POST))
    fun set(@RequestBody ui: UiDoc) {
        val docEntity = repository.findByDocumentId(ui.documentId!!) ?: DocEntity()
        val entity = ui.toEntity(docEntity, readers)
        repository.save(entity)
    }

}

class UiDoc {
    var type: String? = null
    var documentId: String? = null
    var data: String? = null

    fun toUi(entity: DocEntity?) = apply {
        if(entity == null) {
            return@apply
        }
        this.type = entity.type
        this.documentId = entity.documentId
        this.data = JsonBlobs.toString(entity.data, entity.binary)
    }

    fun toEntity(entity: DocEntity, readers: DocumentReaders): DocEntity {
        entity.type = this.type!!
        entity.documentId = this.documentId!!
        entity.binary = readers.isBinary(entity.type)
        entity.data = JsonBlobs.fromString(this.data!!, entity.binary)
        return entity
    }
}