package com.codeabovelab.tpc.web.jpa

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(JpaConfiguration::class))
class DataTest {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var repo: DocsRepository

    @Test
    fun test() {
//        repo.save(DocEntity(type = "tt", report = "report", documentId = "id", binary = false))
        repo.save(DocEntity())
        val findAll = repo.findAll()
        log.info("entity {}", findAll)
        Assert.assertNotNull(findAll)
        Assert.assertTrue(findAll[0].id > 0)

    }
}