package com.codeabovelab.tpc.tool.ui

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 */
@RequestMapping("/rule")
@RestController
class UiRules {

    @RequestMapping("/list")
    fun list(): List<String> {
        return listOf("1", "2")
    }

    @RequestMapping("/set")
    fun set() {
        throw UnsupportedOperationException("Not implemented yet")
    }
}