package com.codeabovelab.tpc.util

import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 */
class RegexMatcher(
            val pattern: Pattern
) {

    constructor(str: String, flags: Int) : this(Pattern.compile(str, flags))

    fun matcher(cs: CharSequence): Matcher {
        return pattern.matcher(cs)
    }

    /**
     * Return object which invoke `contains(String)` as `matcher(String).lookingAt()`
     * @see Matcher.lookingAt()
     */
    fun lookingAt(): PatternSet {
        return decorate(Matcher::lookingAt)
    }

    /**
     * Return object which invoke `contains(String)` as `matcher(String).matches()`
     * @see Matcher.lookingAt()
     */
    fun matches(): PatternSet {
        return decorate(Matcher::matches)
    }

    private fun decorate(op: Matcher.() -> Boolean): PatternSet {
        return object: PatternSet {

            override operator fun contains(other: CharSequence): Boolean {
                return pattern.matcher(other).op()
            }
        }
    }

    /**
     * Pattern which act as 'set' which contains matched string.
     */
    interface PatternSet {
        operator fun contains(other: CharSequence): Boolean
    }
}
