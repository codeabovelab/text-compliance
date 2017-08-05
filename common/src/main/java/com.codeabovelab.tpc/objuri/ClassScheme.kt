package com.codeabovelab.tpc.objuri

import com.codeabovelab.tpc.util.Asserts
import com.google.common.collect.ImmutableList
import java.net.URLDecoder
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

/**
 */
class ClassScheme<out T : Any>(
        override val name: String = "class",
        vararg factories: KCallable<T>
) : Scheme<T> {

    companion object {
        private val C_STRING = java.lang.String::class.java
    }

    override val definition: SchemeDefinition
    private val map = factories.associateBy { it.returnType.javaType.typeName }

    init {
        val paths = ImmutableList.builder<SchemeDefinition.Path>()
        map.forEach { entry ->
            val args = ImmutableList.builder<SchemeDefinition.Argument>()
            entry.value.parameters.forEach { param ->
                val type = getType(param)
                args.add(SchemeDefinition.Argument(param.name!!, type))
            }
            paths.add(SchemeDefinition.Path(entry.key, args.build()))
        }
        definition = SchemeDefinition(paths = paths.build())
    }

    private fun getType(param: KParameter): SchemeDefinition.Type {
        val clazz = getJavaClass(param)
        if(clazz == C_STRING) {
            return SchemeDefinition.Type.STRING
        }
        if(clazz == java.lang.Float::class.java || clazz == java.lang.Double::class.java) {
            return SchemeDefinition.Type.FLOAT
        }
        if(clazz == java.lang.Byte::class.java ||
            clazz == java.lang.Short::class.java ||
            clazz == java.lang.Integer::class.java ||
            clazz == java.lang.Long::class.java) {
            return SchemeDefinition.Type.INTEGER
        }
        return SchemeDefinition.Type.OBJECT
    }

    override fun create(uri: Uri): T {
        val className = uri.path
        val func = map[className]
        Asserts.notNull(func, "Can not find factory for $className of '$uri'")
        val params = func!!.parameters
        val map = parseQuery(params, uri)
        return func.callBy(map)
    }

    private fun parseQuery(reqParams: List<KParameter>, uri: Uri): Map<KParameter, Any?> {
        val map = HashMap<KParameter, Any?>()
        for (reqParam in reqParams) {
            val str = uri.parameters[reqParam.name!!]
            if (str != null) {
                val obj = fromString(reqParam, str)
                map.put(reqParam, obj)
            }
        }
        return map
    }


    private fun fromString(reqParam: KParameter, str: String): Any {
        val javaClass = getJavaClass(reqParam)
        if (javaClass == C_STRING) {
            return URLDecoder.decode(str, "UTF-8")
        }
        //cache for primitives?
        val fromString = javaClass.getDeclaredMethod("valueOf", C_STRING)
        return fromString.invoke(null, str)
    }

    private fun getJavaClass(reqParam: KParameter): Class<out Any> {
        val classifier = reqParam.type.classifier
        val javaClass = (classifier as KClass<*>).javaObjectType
        return javaClass
    }

    class Builder<T : Any> {
        val name: String = "class"
        val factories: MutableList<KCallable<T>> = ArrayList()

        fun build(): ClassScheme<T> {
            return ClassScheme(
                    name = name,
                    factories = *factories.toTypedArray()
            )
        }
    }
}