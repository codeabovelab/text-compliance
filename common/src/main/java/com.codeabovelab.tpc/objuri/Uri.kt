package com.codeabovelab.tpc.objuri

/**
 */
class Uri private constructor(
        val source: String,
        val scheme: String,
        val path: String,
        val parameters: Parameters
) {
    companion object {

        fun parse(str: String): Uri {
            val schemeEnd = str.indexOf(':')
            val scheme = str.substring(0, schemeEnd)
            val pathEnd = str.indexOf('?', schemeEnd)
            val path : String
            val params = ArrayList<Parameter>()
            if(pathEnd > 0) {
                path = str.substring(schemeEnd + 1, pathEnd)
                parseParameters(pathEnd + 1, str, params)
            } else {
                path = str.substring(schemeEnd + 1, str.length)
            }
            return Uri(
                source = str,
                scheme = scheme,
                path = path,
                parameters = Parameters(params)
            )
        }

        private fun parseParameters(off: Int, str: String, params: ArrayList<Parameter>) {
            var i = off
            while (i < str.length) {
                var keyEnd = str.indexOf('=', i)
                if (keyEnd < 0) {
                    keyEnd = str.length
                    val k = str.substring(i, keyEnd)
                    params.add(Parameter(k, null))
                    break
                }
                var valEnd = str.indexOf('&', keyEnd)
                if(valEnd < keyEnd) {
                    valEnd = str.length
                }
                val k = str.substring(i, keyEnd)
                val v = str.substring(keyEnd + 1, valEnd)
                params.add(Parameter(k, v))
                i = valEnd + 1
            }
        }
    }

    override fun toString(): String {
        return source
    }

    data class Parameters constructor(val list: List<Parameter>) {
        operator fun get(key : String) : String? {
            return list.findLast { it.key == key }?.value
        }
    }

    data class Parameter(
            val key: String,
            val value: String?
    )
}