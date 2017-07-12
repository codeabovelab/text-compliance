package com.codeabovelab.tpc.tool.ui

import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.tool.jpa.DocEntity
import com.codeabovelab.tpc.tool.jpa.DocsRepository
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.util.*

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
        val reader = readers[entity.type]
        ud.data = if(reader.binary) {
            Base64.getEncoder().encodeToString(entity.data)
        } else {
            String(entity.data, StandardCharsets.UTF_8)
        }
        return ud
    }

    fun toEntity(ui: UiDoc?): DocEntity? {
        if(ui == null) {
            return null
        }
        val entity = DocEntity()
        entity.type = ui.type!!
        entity.documentId = ui.documentId!!
        val reader = readers[entity.type]
        entity.data = if(reader.binary) {
            Base64.getDecoder().decode(ui.data!!)
        } else {
            ui.data!!.toByteArray(StandardCharsets.UTF_8)
        }
        return entity
    }
}

class UiDoc {
    var type: String? = null
    var documentId: String? = null
    var data: String? = null
}