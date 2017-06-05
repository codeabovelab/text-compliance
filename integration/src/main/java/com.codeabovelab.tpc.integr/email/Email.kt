package com.codeabovelab.tpc.integr.email

import java.util.*
import java.util.stream.Collectors

class Email(val fragments: List<Fragment> = ArrayList()) {

    fun visibleText(): String {
        return filter(false)

    }

    fun hiddenText(): String {
        return filter(true)
    }

    private fun filter(hidden: Boolean): String {
        return fragments.stream()
                .filter { f -> hidden == f.hidden }
                .map { f -> f.content }
                .collect(Collectors.joining("\n"))
    }

    override fun toString(): String {
        return "Email(fragments=$fragments)"
    }
}

