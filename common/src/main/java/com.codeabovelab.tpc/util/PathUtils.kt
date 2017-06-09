package com.codeabovelab.tpc.util

import java.nio.file.Path

/**
 */
object PathUtils {

    fun extension(path: Path) = path.toString().substringAfterLast('.')

    fun nameAndExtension(path: Path): Pair<String, String> {
        val str = path.toString()
        val dotPos = str.lastIndexOf('.')
        if(dotPos < 0) {
            return Pair(str, "")
        }
        return Pair(str.substring(0, dotPos), str.substring(dotPos + 1))
    }

}