package com.codeabovelab.tpc.util

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.superclasses

/**
 */
object Reflections {
    /**
     * Iterate over all property values of specified object and it's childs.
     * It visit same object only one time.
     * @param handler return true when we must go deeper
     */
    fun forEach(obj: Any, handler: ObjectVisitor.() -> Boolean) {
        ObjectVisitor(handler).visit(obj)
    }

    /**
     * Detect when specified type is kotlin or jvm built-in
     */
    fun isBuiltIn(type: KClass<*>): Boolean {
        // obviously anybody can create own package with same name, but we do not found any
        // other way to detect built in types
        val packageName = type.java.`package`.name.substringBefore('.')
        return packageName == "kotlin" ||
                packageName == "java" ||
                packageName == "javax" ||
                packageName == "sun"
    }

    class ObjectVisitor internal constructor(
            private val handler: ObjectVisitor.() -> Boolean
    ) {

        private val STUB : (value: Any?) -> Unit = {}
        private val visited = Collections.newSetFromMap(IdentityHashMap<Any, Boolean>())
        private var prop: KProperty<*>? = null
        private var propVal: Any? = null
        private var propSetter: ((value: Any?) -> Unit)? = null
        val property: KProperty<*>
            get() = prop!!
        var propertyValue: Any
            get() = propVal!!
            set(value) = propSetter!!(value)

        internal fun visit(obj: Any) {
            val type = obj::class
            visitProps(type, obj)
            for(superClass in type.superclasses) {
                visitProps(superClass, obj)
            }
        }


        private fun visitProps(type: KClass<out Any>, obj: Any) {
            if(isBuiltIn(type)) {
                return
            }
            for (prop in type.declaredMemberProperties) {
                if (prop.visibility != KVisibility.PUBLIC) {
                    continue
                }
                this.propVal = prop.getter.call(obj)
                if (propVal == null || !visited.add(propVal)) {
                    continue
                }
                this.propSetter = if(prop is KMutableProperty1) {{
                    prop.setter.call(obj, it)
                }} else STUB
                this.prop = prop
                val deeper = this.handler()
                if (deeper) {
                    visit(propVal!!)
                }
            }
        }
    }
}
