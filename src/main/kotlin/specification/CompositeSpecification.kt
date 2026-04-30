package com.mamonov.specification

import com.mamonov.model.Center

class CompositeSpecification(
    private val first: Specification,
    private val second: Specification
) : Specification {
    override fun isSatisfiedBy(center: Center): Boolean {
        return first.isSatisfiedBy(center) && second.isSatisfiedBy(center)
    }
}