package com.mamonov.specification

import com.mamonov.model.Center

class DistrictSpecification(private val district: String) : Specification {
    override fun isSatisfiedBy(center: Center): Boolean {
        return center.district == district
    }
}