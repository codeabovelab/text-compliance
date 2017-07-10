package com.codeabovelab.tpc.tool.ui

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 */
@ComponentScan(basePackageClasses = arrayOf(UiConfiguration::class))
@Configuration
open class UiConfiguration {
}