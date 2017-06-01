package com.codeabovelab.tpc.tool

import com.codeabovelab.tpc.tool.Classify
import com.codeabovelab.tpc.tool.learn.Learning
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.Option
import java.util.*

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

    fun run() {
        command!!.create(this)()
    }

    enum class Command {
        learn {
            override fun create(cmd: Cmd): () -> Unit {
                Objects.requireNonNull(cmd.in_path, "'i' is required")
                Objects.requireNonNull(cmd.out_path, "'o' is required")
                return Learning(
                        srcDir = cmd.in_path!!,
                        filePath = cmd.out_path!!,
                        config = cmd.config
                )::run
            }
        },
        classify {
            override fun create(cmd: Cmd): () -> Unit {
                Objects.requireNonNull(cmd.in_path, "'i' is required")
                Objects.requireNonNull(cmd.learned, "'l' is required")
                return Classify(cmd.in_path!!, cmd.learned!!)::run
            }

        }
        ;
        abstract fun create(cmd: Cmd): () -> Unit
    }
}