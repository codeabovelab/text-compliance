package com.codeabovelab.tpc.tool.learn.sentiment

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.MappingJsonFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class PrepareReviewData(private val dataDirectory: Path, private val outputDirectory: Path) {

    private val logger = LoggerFactory.getLogger(PrepareReviewData::class.java)

    fun parse() {
        val deferred = Files.walk(dataDirectory)
                .filter { p -> p.toFile().isFile }
                .map { p ->
                    async(CommonPool) {
                        parse(p)
                    }
                }.collect(Collectors.toList())
        runBlocking {
            val sum = deferred.sumBy { it.await() }
            logger.info("result: {}", sum)
        }
    }

    private suspend fun parse(p: Path): Int {
        val mapper = MappingJsonFactory().configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
        val jp = mapper.createParser(p.toFile())
        var i = 0
        while (jp.nextToken() == JsonToken.START_OBJECT) {
            i = parse(jp = jp, name = p.toFile().name, i = i)
        }
        logger.info("prepared: {}, from: {}", i, p.toFile().name)
        return i
    }

    private fun parse(jp: JsonParser, name: String, i: Int): Int {
        val review = Review(fileName = name)
        while (jp.nextToken() !== JsonToken.END_OBJECT) {
            when (jp.currentName) {
                "overall" -> review.overall = jp.valueAsDouble.toInt()
                "reviewerID" -> review.reviewerID = jp.valueAsString
                "reviewText" -> review.reviewText = jp.valueAsString
            }
        }
        return save(review, i)
    }

    private fun save(review: Review, i: Int): Int {
        return when {
            review.overall < 3 -> save("neg", review, i)
            review.overall > 3 -> save("pos", review, i)
            else -> return i
        }
    }

    private fun save(type: String, review: Review, i: Int): Int {
        if (review.reviewText.isNullOrBlank()) {
            logger.warn("review is empty")
            return i
        }
        val file = File(outputDirectory.toFile(),
                "${calcType(i)}/$type/${review.fileName}/${review.overall}__${review.reviewerID}")
        file.parentFile.mkdirs()
        file.writeText(review.reviewText)
        return i + 1
    }

    private fun calcType(i: Int): String {
        return if (i % 3 == 0) "test" else "train"
    }

    data class Review(var overall: Int = 3, var reviewText: String = "", var fileName: String = "", var reviewerID: String = "")
}
