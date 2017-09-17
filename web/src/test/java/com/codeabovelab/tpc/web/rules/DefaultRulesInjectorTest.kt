package com.codeabovelab.tpc.web.rules

import com.codeabovelab.tpc.web.jpa.RulesRepository
import com.codeabovelab.tpc.web.ui.UiRule
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.springframework.core.io.ResourceLoader

/**
 */
class DefaultRulesInjectorTest {
    @Test
    fun testLoad() {
        val ri = DefaultRulesInjector(
                repository = Mockito.mock(RulesRepository::class.java),
                loader = Mockito.mock(ResourceLoader::class.java)
        )
        val src = Thread.currentThread().contextClassLoader.getResourceAsStream("config/rules-default.yaml")
        val rules = ri.load(src)
        println(rules)
        var defRules = DefaultRulesInjector.Rules(listOf(
                UiRule(
                        ruleId = "text-classification",
                        weight = 1f,
                        predicate = """{"@type":"TextClassifierPredicate"}""",
                        action = null,
                        description = """Relation to business conversation
Rule show relation to business conversation texts.
  1 - closest relation
  0 - no relation
  -1 - negative relation
""",
                        enabled = true,
                        child = false
                ),
                UiRule(
                        ruleId = "keywords",
                        weight = 1f,
                        predicate = """{"@type":"WordPredicate"}""",
                        action = null,
                        description = """Classify text by keywords
Classify text by contained keywords. Currently support follow labels:
    offensive-lang: 1 - has swear words, 0 - no swear words
""",
                        enabled = true,
                        child = false
                ),
                UiRule(
                        ruleId = "3rd-party-brand",
                        weight = 1f,
                        predicate = """{"@type":"RegexPredicate","pattern":"BRAND"}""",
                        action = null,
                        description = "Detect 3rd party brands mention",
                        enabled = true,
                        child = false
                ),
                UiRule(
                        ruleId = "sentiment-classification",
                        weight = 1f,
                        predicate = """{"@type":"SentimentClassifierPredicate"}""",
                        action = null,
                        description = "Sentiment classification: 1 - negative, 0 - positive",
                        enabled = true,
                        child = false
                )
        ))
        assertEquals(defRules, rules)

    }
}