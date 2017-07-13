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
        return toUi(repository.findByDocumentId(id))
    }

    @RequestMapping("/set", method = arrayOf(RequestMethod.POST))
    fun set(@RequestBody ui: UiDoc) {
        val entity = toEntity(ui)
        repository.save(entity)
    }


    fun toUi(entity: DocEntity?): UiDoc? {
        if(entity == null) {
            return null
        }
        val ud = UiDoc()
        ud.type = entity.type
        ud.documentId = entity.documentId
        ud.data = JsonBlobs.toString(entity.data, entity.binary)
        return ud
    }

    fun toEntity(ui: UiDoc?): DocEntity? {
        if(ui == null) {
            return null
        }
        val entity = DocEntity()
        entity.type = ui.type!!
        entity.documentId = ui.documentId!!
        entity.data = JsonBlobs.fromString(ui.data!!, readers.isBinary(entity.type))
        return entity
    }
}

class UiDoc {
    var type: String? = null
    var documentId: String? = null
    var data: String? = null
}