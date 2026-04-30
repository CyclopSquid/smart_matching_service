package com.mamonov.strategy

import com.mamonov.model.Center
import com.mamonov.model.CenterScore

class ClosestFirstStrategy : RankingStrategy {
    override fun rank(scored: List<CenterScore>, centers: List<Center>): List<CenterScore> {
        return scored.sortedBy { centerScore ->
            centers.find { it.id == centerScore.centerId }?.distanceKm ?: Double.MAX_VALUE
        }
    }
}