package com.codeabovelab.tpc.objuri

/**
 */
class SchemeDefinition(
        val paths: List<Path>
) {
    class Path(
            val name: String,
            val arguments: List<Argument>
    )

    class Argument(
            val name: String,
            val type: Type
    )

    enum class Type {
        STRING, INTEGER, FLOAT, OBJECT
    }
}

