package com.codeabovelab.tpc.tool.util

/**
 * Mark property which has path to resource,which must be copied to result data
 */
@Target(allowedTargets = AnnotationTarget.PROPERTY)
annotation class Copy(
        /**
         * place copy under specified directory
         */
        val dir: String = ""
)