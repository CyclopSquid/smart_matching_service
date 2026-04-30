package com.mamonov.strategy

import com.mamonov.model.Center
import com.mamonov.model.CenterScore

class BalancedStrategy : RankingStrategy {
    override fun rank(scored: List<CenterScore>, centers: List<Center>): List<CenterScore> {
        return scored.sortedByDescending { it.score }
    }
}