package com.codeabovelab.tpc.util

import java.util.*
import kotlin.reflect.KProperty

/**
 */
object Asserts {

    private fun <T> propName(prop: KProperty<T>): String {
        //how to obtain classname here
        return prop.name
    }

    fun <T> notNull(prop: KProperty<T>): T {
        val value = prop.getter.call()
        val name = propName(prop)
        Objects.requireNonNull(value, "'$name' is required")
        return value
    }

    fun notNullAll(vararg props: KProperty<*>) {
        var sb: StringBuilder? = null
        for(prop in props) {
            val value = prop.getter.call()
            val name = propName(prop)
            if(value == null) {
                if(sb == null) {
                    sb = StringBuilder()
                } else {
                    sb.append(", ")
                }
                sb.append(name)
            }
        }
        if(sb != null) {
            sb.append(" is required")
            throw IllegalArgumentException(sb.toString())
        }
    }

    fun notNull(any: Any?, message: String) {
        if(any == null) {
            throw NullPointerException(message)
        }
    }
}