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
    var in_path: String? = null

    @field:Option(name = "-o", usage = "path to output data")
    var out_path: String? = null

    @field:Option(name = "-l", usage = "path to learned data")
    var learned: String? = null

    @field:Option(name = "-c", usage = "path to config file")
    var config: String? = null

    @field:Option(name = "-w", usage = "list of words properties")
    var words: String? = null

    fun run() {
        command!!.create(this)()
    }

    enum class Command {
        learn {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::in_path, cmd::out_path)
                return Learning(
                        srcDir = cmd.in_path!!,
                        destDir = cmd.out_path!!,
                        config = cmd.config
                )::run
            }
        },
        classify {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::in_path, cmd::learned)
                return Classify(
                        in_data = cmd.in_path!!,
                        in_learned = cmd.learned!!
                )::run
            }

        },
        process {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::in_path, cmd::learned)

                return Process(
                        inData = cmd.in_path!!,
                        outData = cmd.out_path,
                        learned = cmd.learned!!,
                        words = cmd.words,
                        config = cmd.config
                )::run
            }
        },
        nlp {
            override fun create(cmd: Cmd): () -> Unit {
                require(cmd::in_path)
                return Nlp(
                        inDir = cmd.in_path!!,
                        outDir = cmd.out_path
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