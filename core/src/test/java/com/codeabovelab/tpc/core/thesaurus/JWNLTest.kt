package com.codeabovelab.tpc.core.thesaurus

import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Test

class JWNLTest {

    @Test
    fun testSynonyms() {
        val syn = WordSynonyms(JwnlThesaurusDictionary.DictionaryResolver)
        val lookup = syn.lookup("hell")
        Assert.assertNotNull(lookup)
        assertThat(lookup.words, containsInAnyOrder(
                "hellhole",
                "netherworld",
                "the pits",
                "perdition",
                "snake pit",
                "scheol",
                "hell",
                "hell on earth",
                "infernal region",
                "nether region",
                "blaze",
                "underworld",
                "pit",
                "hades",
                "inferno"))
    }
}