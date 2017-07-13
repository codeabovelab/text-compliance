package com.codeabovelab.tpc.util

import java.nio.charset.StandardCharsets
import java.util.*

/**
 */
object JsonBlobs {
    fun fromString(src: String, binary: Boolean): ByteArray {
        if(binary) {
            return Base64.getDecoder().decode(src)
        } else {
            return src.toByteArray(StandardCharsets.UTF_8)
        }
    }

    fun toString(src: ByteArray, binary: Boolean): String {
        if(binary) {
            return Base64.getEncoder().encodeToString(src)
        } else {
            return String(src, StandardCharsets.UTF_8)
        }
    }
}