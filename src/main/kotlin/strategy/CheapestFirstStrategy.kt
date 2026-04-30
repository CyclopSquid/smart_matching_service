package com.mamonov.strategy

import com.mamonov.model.Center
import com.mamonov.model.CenterScore

class CheapestFirstStrategy : RankingStrategy {
    override fun rank(scored: List<CenterScore>, centers: List<Center>): List<CenterScore> {
        return scored.sortedBy { scored ->
            centers.find { it.id == scored.centerId }?.pricePerDay ?: Int.MAX_VALUE
        }
    }
}