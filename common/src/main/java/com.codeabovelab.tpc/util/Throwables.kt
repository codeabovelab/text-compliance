package com.codeabovelab.tpc.util


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

/**
 * tools for throwables
 */
object Throwables {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Print specified throwable to string. If throwable is null, then return null.
     * @param e
     * @return
     */
    fun printToString(e: Throwable): String {
        val sw = StringWriter()
        return try {
            PrintWriter(sw).use(e::printStackTrace)
            sw.toString()
        } catch (ioe: IOException) {
            // usually this code used for processing catched throwables
            //   therefore we don't need another annoying exception
            "can not print exception due: $ioe"
        }
    }

    /**
     * Wrap generic exception to RuntimeException, or cast and return it.
     * @param e
     * @return
     */
    fun asRuntime(e: Throwable): RuntimeException {
        if(e is RuntimeException) {
            return e
        }
        return RuntimeException(e)
    }

    /**
     * Find in chain of causes first instance of specified type.
     * @param e
     * @param type
     * @return instance of specified type or null.
     */
    fun <T : Throwable> find(e: Throwable, type : Class<T>): T? {
        var tmp: Throwable? = e
        while(tmp != null) {
            if(type.isInstance(tmp)) {
                return type.cast(tmp)
            }
            tmp = tmp.cause
        }
        return null
    }

    /**
     * Test that specified throwable has specified type in chain of causes.
     * @param e
     * @param type
     * @return true if throwable has specified type in chain of causes.
     */
    fun has(e : Throwable, type : Class<in Throwable> ) : Boolean {
        var tmp: Throwable? = e
        while(tmp != null) {
            if(type.isInstance(e)) {
                return true
            }
            tmp = tmp.cause
        }
        return false
    }

    fun uncaughtHandler(): Thread.UncaughtExceptionHandler {
        return uncaughtHandler(log)
    }

    fun uncaughtHandler(log: Logger): Thread.UncaughtExceptionHandler {
        return uncaughtHandler(log, "Uncaught exception.")
    }

    fun uncaughtHandler(log: Logger, msg: String): Thread.UncaughtExceptionHandler {
        return Thread.UncaughtExceptionHandler { _, ex ->
            log.error(msg, ex)
        }
    }
}
