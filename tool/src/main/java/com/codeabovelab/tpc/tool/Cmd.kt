package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.tool.learn.Learning
import com.codeabovelab.tpc.tool.nlp.Nlp
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.Option
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 */
class Cmd {

    @field:Argument(required = true, usage = "command ")
    var command: Command? = null

    @field:Option(name = "-i", usage = "path to input data")
    var inPath: String? = null

    @field:Option(name = "-o", usage = "path to output data")
    var outPath: String? = null

    @field:Option(name = "-l", usage = "path to learned data")
    var learned: String? = null

    @field:Option(name = "-c", usage = "path to config file")
    var config: String? = null

    fun run() {
        command!!.create(this)()
    }

    enum class Command {
        learn {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::inPath, cmd::outPath)
                return Learning(
                        srcDir = cmd.inPath!!,
                        destDir = cmd.outPath!!,
                        config = cmd.config
                )::run
            }
        },
        classify {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::inPath, cmd::learned)
                return Classify(
                        inData = cmd.inPath!!,
                        inLearned = cmd.learned!!
                )::run
            }

        },
        process {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::inPath, cmd::learned)

                return Process(
                        inData = cmd.inPath!!,
                        outData = cmd.outPath,
                        learned = cmd.learned!!
                )::run
            }
        },
        nlp {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::inPath)
                return Nlp(
                        inDir = cmd.inPath!!,
                        outDir = cmd.outPath
                )::run
            }

        },
        evaluate {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::learned)
                return Evaluate(
                        inLearned = cmd.learned!!,
                        inData = cmd.inPath
                )::run
            }
        }
        ;

        abstract fun create(cmd: Cmd): () -> Unit
    }

    companion object {
        fun require(vararg props: KProperty<*>) {
            props.forEach { prop ->
                val value = prop.getter.call()
                val ann = prop.javaField!!.getAnnotation(Option::class.java)
                Objects.requireNonNull(value, "'${ann!!.name}' is required")
            }
        }
    }
}