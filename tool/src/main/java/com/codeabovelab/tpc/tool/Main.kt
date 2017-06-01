package com.codeabovelab.tpc.tool

import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser

/**
 */
fun main(args: Array<String>) {
    val cmd = Cmd()
    val cmdParser = CmdLineParser(cmd)
    try {
        cmdParser.parseArgument(*args)
    } catch(e: CmdLineException) {
        println("Error: ${e.message}")
        print("Usage: java -jar tool.jar ")
        cmdParser.printSingleLineUsage(System.out)
        println()
        cmdParser.printUsage(System.out)
        return
    }
    cmd.run()
}