package com.codeabovelab.tpc.core.kw

import com.codeabovelab.tpc.core.nn.nlp.WordContext
import com.google.common.base.Splitter
import java.util.*

class KeywordSetMatcher private constructor(
        private val map: Map<String, Set<String>>
) : KeywordMatcher {

    private val SPLIT_COMA = Splitter.on(',').trimResults()
    private val SPLIT_NL = Splitter.on('\n').omitEmptyStrings().trimResults()
    private val replace = makeReplaces("""
0-o
5-s
3-e
1,!-i,l
4,@-a
+-t
""")

    private fun makeReplaces(s: String): Map<Char, String> {
        val map = TreeMap<Char, String>()
        for(line in SPLIT_NL.split(s)) {
            val from = line.substringBefore('-')
            val to = line.substringAfter('-')
            for(fc in SPLIT_COMA.split(from)) {
                for(tc in SPLIT_COMA.split(to)) {
                    map.put(fc.first(), tc)
                }
            }
        }
        return map
    }

    override fun test(wc: WordContext): Set<String> {
        var word = wc.word.lemma?: wc.word.str
        word = clear(word)
        val res = map[word]
        return res ?: Collections.emptySet()
    }

    private fun clear(str: String): String {
        var i = 0
        var buff: StringBuilder? = null
        while(i < str.length) {
            val char = str[i]
            i++
            val to = replace[char]
            if(to == null) {
                if(buff != null) {
                    buff.append(char)
                }
                continue
            }
            if(buff == null) {
                buff = StringBuilder()
                buff.append(str, 0, i)
            }
            buff.append(to)
        }
        if(buff == null) {
            return str
        }
        return buff.toString()
    }


    class Builder {
        internal val map = TreeMap<String, TreeSet<String>>(String.CASE_INSENSITIVE_ORDER)

        fun add(word: String, labels: Iterable<String>) = apply {
            map.compute(word) { _, old ->
                val set = old ?: TreeSet<String>(String.CASE_INSENSITIVE_ORDER)
                set.addAll(labels)
                set
            }
        }

        fun build(): KeywordSetMatcher {
            val map = TreeMap<String, Set<String>>(String.CASE_INSENSITIVE_ORDER)
            for((key, value) in this.map) {
                @Suppress("UNCHECKED_CAST")
                map.put(key, Collections.unmodifiableSet(value.clone() as TreeSet<String>))
            }
            return KeywordSetMatcher(Collections.unmodifiableMap(map))
        }

    }

}