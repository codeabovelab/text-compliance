package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.doc.Document
import com.codeabovelab.tpc.doc.DocumentReaders
import com.codeabovelab.tpc.web.jpa.DocEntity
import com.codeabovelab.tpc.web.jpa.DocsRepository
import com.codeabovelab.tpc.util.JsonBlobs
import com.codeabovelab.tpc.web.docs.DocsStorage
import com.codeabovelab.tpc.web.docs.ThreadResolverService
import io.swagger.annotations.ApiOperation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.MimeTypeUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.nio.charset.StandardCharsets
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.time.LocalDateTime


/**
 */
@RequestMapping( path = arrayOf("/doc"), produces = arrayOf(MimeTypeUtils.APPLICATION_JSON_VALUE))
@Transactional(propagation = Propagation.REQUIRED)
@RestController
open class UiDocumentController(
        private var repository: DocsRepository,
        private val threadResolver: ThreadResolverService,
        private val docStorage: DocsStorage,
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

    @RequestMapping("/download", method = arrayOf(RequestMethod.GET))
    fun download(id: String): ResponseEntity<StreamingResponseBody> {
        val entity = repository.findByDocumentId(id)
        if(entity == null) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        val headers = HttpHeaders()
        headers.contentType = MediaType.valueOf(entity.type)
        val filename = getFileName(entity)
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''$filename")
        return ResponseEntity(StreamingResponseBody { os ->
            os.write(entity.data)
        }, headers, HttpStatus.OK)
    }

    private fun getFileName(entity: DocEntity): String {
        var name = entity.filename
        if(name == null) {
            name = "doc-${entity.id}.data"
        }
        return URLEncoder.encode(name, StandardCharsets.UTF_8.name())
    }

    @RequestMapping("/upload", method = arrayOf(RequestMethod.POST))
    fun uploadSource(
            @RequestPart("file") file: MultipartFile,
            @RequestParam(name = "id", required = false) id: String?
    ) : UiDocHeader {
        // we must not upload to already existed document!
        if(id != null && repository.findByDocumentId(id) != null) {
            throw IllegalAccessException("Document with $id already exists.")
        }
        val entity = DocEntity()
        entity.type = file.contentType
        entity.date = LocalDateTime.now()
        entity.filename = file.originalFilename
        val reader = readers[entity.type]!!
        entity.binary = reader.info.binary
        if(file.size >= DocEntity.MAX_DOC_SIZE) {
            throw IllegalArgumentException("Too big file: ${file.size}, max: ${DocEntity.MAX_DOC_SIZE}")
        }
        entity.data = file.inputStream.use { it.readBytes(file.size.toInt()) }

        //test that document is readable and have correct id
        val doc = entity.data.inputStream().use {
            reader.read(id, it)
        }.build()
        entity.documentId = doc.id
        if(id != null && doc.id != id) {
            throw IllegalArgumentException("Doc id '${doc.id}' different from specified '$id'.")
        }
        repository.save(entity)
        val dh = UiDocHeader()
        dh.fromEntity(entity)
        return dh
    }

    @ApiOperation("Give text document representation")
    @RequestMapping("/text", method = arrayOf(RequestMethod.GET))
    fun text(id: String): Document? {
        return docStorage[id]
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

    @RequestMapping("/related", method = arrayOf(RequestMethod.GET))
    fun getRelated(id: String): List<String> {
        return threadResolver.getRelated(id)
    }
}

fun DocEntity?.toUi(): UiDoc? {
    if(this == null) {
        return null
    }
    val ui = UiDoc()
    ui.fromEntity(this)
    ui.data = JsonBlobs.toString(data, binary)
    return ui
}

class UiDoc : UiDocHeader() {
    var data: String? = null

    fun toEntity(entity: DocEntity, readers: DocumentReaders): DocEntity {
        entity.type = this.type!!
        entity.documentId = this.documentId!!
        entity.binary = readers.isBinary(entity.type)
        entity.date = date!!
        entity.data = JsonBlobs.fromString(this.data!!, entity.binary)
        return entity
    }
}

open class UiDocHeader {
    var type: String? = null
    var date: LocalDateTime? = null
    var documentId: String? = null

    fun fromEntity(entity: DocEntity) {
        type = entity.type
        documentId = entity.documentId
        date = entity.date
    }
}