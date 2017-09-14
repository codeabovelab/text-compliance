package com.codeabovelab.tpc.web.objf

import com.codeabovelab.tpc.util.Asserts
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.cast

/**
 * Tool for create object from standard json representation. <p/>
 */
class ObjFactory<T : Any> private constructor(
        builder: Builder<T>
) {
    companion object {
        private val C_STRING = java.lang.String::class.java
    }

    private val type = builder.type
    val definition: FactoriesDefinition
    private val map = builder.factories.associateBy {
        val clazz = toJava(it.returnType)
        clazz.getAnnotation(JsonTypeName::class.java)?.value ?: clazz.name
    }
    private val mapper: ObjectMapper

    init {
        mapper = builder.objectMapper ?: throw IllegalArgumentException("Require non null 'objectMapper'")
        if(!type.java.isAnnotationPresent(JsonTypeInfo::class.java)) {
            // fix root annotation
            mapper.addMixIn(type.java, MixIn::class.java)
        }

        val paths = ImmutableList.builder<FactoriesDefinition.Factory>()
        map.forEach { entry ->
            val args = ImmutableList.builder<FactoriesDefinition.Argument>()
            entry.value.parameters.forEach { param ->
                val type = getType(param)
                args.add(FactoriesDefinition.Argument(param.name!!, type))
            }
            paths.add(FactoriesDefinition.Factory(entry.key, args.build()))
        }
        definition = FactoriesDefinition(factories = paths.build())
    }

    private fun getType(param: KParameter): FactoriesDefinition.Type {
        val clazz = getJavaClass(param)
        if(clazz == C_STRING) {
            return FactoriesDefinition.Type.STRING
        }
        if(clazz == java.lang.Float::class.java || clazz == java.lang.Double::class.java) {
            return FactoriesDefinition.Type.FLOAT
        }
        if(clazz == java.lang.Byte::class.java ||
                clazz == java.lang.Short::class.java ||
                clazz == java.lang.Integer::class.java ||
                clazz == java.lang.Long::class.java) {
            return FactoriesDefinition.Type.INTEGER
        }
        return FactoriesDefinition.Type.OBJECT
    }

    private fun parseQuery(reqParams: List<KParameter>, tree: JsonNode): Map<KParameter, Any?> {
        val map = HashMap<KParameter, Any?>()
        for (reqParam in reqParams) {
            val node = tree[reqParam.name!!]
            if (node == null) {
                continue
            }
            val type = getJavaClass(reqParam)
            val obj = mapper.reader().treeToValue(node, type)
            map.put(reqParam, obj)
        }
        return map
    }

    private fun getJavaClass(reqParam: KParameter): Class<out Any> {
        val type = reqParam.type
        return toJava(type)
    }

    private fun toJava(type: KType): Class<out Any> {
        val classifier = type.classifier
        val javaClass = (classifier as KClass<*>).javaObjectType
        return javaClass
    }

    /**
     * Inline variant of [readRaw] with cast to [R]
     */
    inline fun <reified R: T> read(string: String) : R {
        val obj = this.readRaw(string)
        return R::class.cast(obj)
    }

    fun readRaw(string: String): T {
        val reader = mapper.reader()
        val tree = reader.readTree(string)
        val className = tree["@type"].textValue()
        val func = map[className]
        Asserts.notNull(func, "Can not find factory for $className of '$string'. Available: ${map.keys}")
        val params = func!!.parameters
        val map = parseQuery(params, tree)
        val obj = func.callBy(map)
        return obj
    }

    fun write(value: T): String {
        return mapper.writeValueAsString(value)
    }

    class Builder<T : Any>(val type: KClass<T>) {
        val factories: MutableList<KCallable<T>> = ArrayList()
        var objectMapper: ObjectMapper? = null

        fun build(): ObjFactory<T> {
            return ObjFactory(this)
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    private class MixIn
}