package com.codeabovelab.tpc.util

import kotlin.reflect.KProperty

/**
 */
class SynchronizedDelegate<in R: Any, T: Any?> {

    private var value : T? = null

    operator fun getValue(thisRef: R, property: KProperty<*>): T? {
        return synchronized(thisRef) {
            value
        }
    }

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T?) {
        return synchronized(thisRef) {
            this.value = value
        }
    }
}