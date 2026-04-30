package com.mamonov.strategy

import com.mamonov.model.Center
import com.mamonov.model.CenterScore

class BestRatingStrategy : RankingStrategy {
    override fun rank(scored: List<CenterScore>, centers: List<Center>): List<CenterScore> {
        return scored.sortedByDescending { scored ->
            centers.find { it.id == scored.centerId }?.rating ?: 0.0
        }
    }
}