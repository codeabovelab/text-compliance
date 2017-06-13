package com.codeabovelab.tpc.core.thesaurus

import com.google.common.io.Resources
import net.didion.jwnl.JWNL
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class JWNLTest {

    @Before
    fun setUp() {
        val resource = Resources.getResource(this.javaClass, "jwnl.xml")
        JWNL.initialize(resource.openStream())
    }

    @Test
    @Ignore
    fun testSynonyms() {
        val syn = JWNLWordSynonyms()
        val lookup = syn.lookup("hell")
        Assert.assertNotNull(lookup)
        assertThat(lookup.words, containsInAnyOrder(
                "hellhole", "netherworld", "perdition", "scheol", "hell", "blaze", "underworld", "sin", "pit", "inferno", "hades"))
    }

}