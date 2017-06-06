package com.codeabovelab.tpc.core.nn.nlp

/**
 * Part-of-speech
 */
enum class Pos {
    /**
    Coordinating conjunction
     */
    CC,
    /**
    Cardinal number
     */
    CD,
    /**
    Determiner
     */
    DT,
    /**
    Existential there
     */
    EX,
    /**
    Foreign word
     */
    FW,
    /**
    Preposition or subordinating conjunction
     */
    IN,
    /**
    Adjective
     */
    JJ,
    /**
    Adjective, comparative
     */
    JJR,
    /**
    Adjective, superlative
     */
    JJS,
    /**
    List item marker
     */
    LS,
    /**
    Modal
     */
    MD,
    /**
    Noun, singular or mass
     */
    NN,
    /**
    Noun, plural
     */
    NNS,
    /**
    Proper noun, singular
     */
    NNP,
    /**
    Proper noun, plural
     */
    NNPS,
    /**
    Predeterminer
     */
    PDT,
    /**
    Possessive ending
     */
    POS,
    /**
    Personal pronoun
     */
    PRP,
    /**
    Possessive pronoun
     */
    `PRP$`,
    /**
    Adverb
     */
    RB,
    /**
    Adverb, comparative
     */
    RBR,
    /**
    Adverb, superlative
     */
    RBS,
    /**
    Particle
     */
    RP,
    /**
    Symbol
     */
    SYM,
    /**
    to
     */
    TO,
    /**
    Interjection
     */
    UH,
    /**
    Verb, base form
     */
    VB,
    /**
    Verb, past tense
     */
    VBD,
    /**
    Verb, gerund or present participle
     */
    VBG,
    /**
    Verb, past participle
     */
    VBN,
    /**
    Verb, non­3rd person singular present
     */
    VBP,
    /**
    Verb, 3rd person singular present
     */
    VBZ,
    /**
    Wh­determiner
     */
    WDT,
    /**
    Wh­pronoun
     */
    WP,
    /**
    Possessive wh­pronoun
     */
    `WPS$`,
    /**
    Wh­adverb
     */
    WRB,
    UNKNOWN;

    companion object {
        fun parse(str: String?): Pos {
            if(str.isNullOrBlank()) {
                return UNKNOWN
            }
            try {
                var s = str!!.toUpperCase()
                return Pos.valueOf(s)
            } catch(e: Exception) {
                return UNKNOWN
            }
        }
    }

    val isNoun: Boolean get() {
        return name.first() == 'N'
    }

    val isVerb: Boolean get() {
        return name.first() == 'V'
    }
}