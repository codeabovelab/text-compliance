package com.codeabovelab.tpc.objuri

import com.codeabovelab.tpc.util.Asserts
import java.util.*
import kotlin.reflect.full.cast

/**
 * Tool for create object by its uri. <p/>
 * Examples (possibly not implemented):
 * <pre>
 *     class:java.lang.Integer?value=1
 *     json:{'key':'value'}
 * </pre>
 */
class ObjUri<T : Any>(vararg schemes: Scheme<T>) {
    val map: Map<String, Scheme<T>> = Collections.unmodifiableMap(schemes.associateBy(Scheme<T>::name))

    inline fun <reified R : T> create(uriString: String) : R {
        val uri = Uri.parse(uriString)
        val scheme = map[uri.scheme]
        Asserts.notNull(scheme, "Can not find scheme of '$uri'")
        val obj = try {
            scheme!!.create(uri)
        } catch (e : Exception) {
            throw RuntimeException("Can not create object from '$uri'", e)
        }
        return R::class.cast(obj)
    }
}