package com.codeabovelab.tpc.web.ui

import com.codeabovelab.tpc.web.jpa.RuleEntity
import com.codeabovelab.tpc.web.jpa.RulesRepository
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