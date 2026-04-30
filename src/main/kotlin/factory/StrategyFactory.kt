package com.mamonov.factory

import com.mamonov.strategy.*

object StrategyFactory {
    fun create(type: String): RankingStrategy = when (type.lowercase()) {
        "cheapest" -> CheapestFirstStrategy()
        "rating"   -> BestRatingStrategy()
        else       -> BalancedStrategy()
    }
}