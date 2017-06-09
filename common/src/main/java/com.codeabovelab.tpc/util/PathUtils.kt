package com.codeabovelab.tpc.util

import java.nio.file.Path

/**
 */
object PathUtils {

    fun extension(path: Path): String {
        val str = path.toString()
        val dotPos = findExt(str, path)
        if(dotPos < 0) {
            return ""
        }
        return str.substring(dotPos + 1)
    }

    fun nameAndExtension(path: Path): Pair<String, String> {
        val str = path.toString()
        val dotPos = findExt(str,  path)
        if(dotPos < 0) {
            return Pair(str, "")
        }
        return Pair(str.substring(0, dotPos), str.substring(dotPos + 1))
    }

    private fun findExt(str: String, path: Path): Int {
        val sep = str.lastIndexOf(path.fileSystem.separator)
        val dotPos = str.lastIndexOf('.')
        return if(dotPos > sep) dotPos else -1
    }

    /**
     * Full path to file, without extension
     */
    fun withoutExtension(path: Path): String {
        val str = path.toString()
        val dotPos = findExt(str, path)
        if(dotPos < 0) {
            return str
        }
        return str.substring(0, dotPos)
    }
}