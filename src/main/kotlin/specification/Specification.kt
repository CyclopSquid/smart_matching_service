package com.mamonov.specification

import com.mamonov.model.Center

interface Specification {
    fun isSatisfiedBy(center: Center): Boolean

    fun and(other: Specification): Specification =
        CompositeSpecification(this, other)
}