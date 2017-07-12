package com.codeabovelab.tpc.tool.ui

import com.codeabovelab.tpc.tool.jpa.RuleEntity
import com.codeabovelab.tpc.tool.jpa.RulesRepository
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 */
@RequestMapping("/rule")
@RestController
class UiRules(
        private var repository: RulesRepository
) {

    @RequestMapping("/list", method = arrayOf(RequestMethod.GET))
    fun list(): List<RuleEntity>? {
        return repository.findAll()
    }

    @RequestMapping("/set", method = arrayOf(RequestMethod.POST))
    fun set(@RequestBody entity: RuleEntity) {
        repository.save(entity)
    }
}