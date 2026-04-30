package com.mamonov.specification

import com.mamonov.model.Center

class ServiceSpecification(private val requiredServices: List<String>) : Specification {
    override fun isSatisfiedBy(center: Center): Boolean {
        return requiredServices.any { it in center.services }
    }
}