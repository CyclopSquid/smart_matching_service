package com.mamonov.specification

import com.mamonov.model.Center

class BudgetSpecification(private val maxBudget: Int) : Specification {
    override fun isSatisfiedBy(center: Center): Boolean {
        return center.pricePerDay <= maxBudget
    }
}