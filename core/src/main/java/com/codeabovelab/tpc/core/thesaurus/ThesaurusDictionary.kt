package com.codeabovelab.tpc.core.thesaurus

import java.io.InputStream
import javax.annotation.concurrent.ThreadSafe

interface ThesaurusDictionary {

    fun lookup(word: String): Set<String>

    /**
     * allows to get instance of dictionary
     */
    @ThreadSafe
    interface Resolver {
        /**
         * optional param for loading external dictionaries
         */
        fun source(source: InputStream): Resolver

        /**
         * always returns working instance cause classpath has default dictionary
         */
        fun resolve(): ThesaurusDictionary
    }

}