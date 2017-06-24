package com.codeabovelab.tpc.core.thesaurus

import com.codeabovelab.tpc.core.thesaurus.ThesaurusDictionary.Resolver
import net.sf.extjwnl.dictionary.Dictionary
import java.io.InputStream
import java.util.concurrent.atomic.AtomicReference

/**
 * extjwnl implementation
 */
class JwnlThesaurusDictionary(val dictionary: Dictionary) : ThesaurusDictionary {

    override fun lookup(word: String): Set<String> {

        val allIndexWords = dictionary.lookupAllIndexWords(word)
        return allIndexWords.indexWordArray
                .flatMap { it.senses.asIterable() }
                .flatMap { it.words.asIterable() }
                .map { it.lemma.toLowerCase() }
                .toHashSet()
    }

    /**
     * Supposed that only one dict will be installed in system
     */
    companion object DictionaryResolver: Resolver {
        private val dict: AtomicReference<JwnlThesaurusDictionary> = AtomicReference()
        private var source: InputStream? = null

        override fun source(source: InputStream) = apply { this.source = source }

        override fun resolve(): ThesaurusDictionary {
            if (source != null) {
                // classpath has default jwn dict
                val previous = dict.getAndSet(JwnlThesaurusDictionary(Dictionary.getInstance(source)))
                freeResources(previous)
            }
            val res = dict.get()
            if (res == null) {
                dict.compareAndSet(null, JwnlThesaurusDictionary(Dictionary.getDefaultResourceInstance()))
            }
            return dict.get()
        }

        private fun freeResources(previous: JwnlThesaurusDictionary?) {
            previous?.dictionary?.close()
        }

    }
}