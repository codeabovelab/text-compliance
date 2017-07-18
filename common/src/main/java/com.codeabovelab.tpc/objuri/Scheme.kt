package com.codeabovelab.tpc.objuri

/**
 * Scheme factory interface. Used for creation object by string specification.
 * @see ObjUri
 */
interface Scheme<out T : Any> {

    /**
     * Name of scheme, used in [ObjUri.create]
     */
    val name: String

    /**
     * Create instance by specified uri
     */
    fun create(uri: Uri) : T
}