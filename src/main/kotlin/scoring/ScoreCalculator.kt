package com.mamonov.scoring

import com.mamonov.model.Center
import com.mamonov.model.MatchingRequest

class ScoreCalculator(
    private val districtWeight: Int = 30,
    private val budgetWeight: Int = 25,
    private val serviceWeight: Int = 40,
    private val ratingWeight: Int = 5
) {
    fun calculate(center: Center, request: MatchingRequest): Int {
        var score = 0

        if (center.district == request.district)
            score += districtWeight

        if (center.pricePerDay <= request.maxBudget)
            score += budgetWeight

        if (request.requiredServices.any { it in center.services })
            score += serviceWeight

        if (center.rating >= request.preferredRating)
            score += ratingWeight

        return score
    }
}