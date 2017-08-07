package com.codeabovelab.tpc.web.objf

/**
 */
class FactoriesDefinition(
        val factories: List<Factory>
) {
    class Factory(
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

