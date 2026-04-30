package com.mamonov.service

import com.mamonov.factory.StrategyFactory
import com.mamonov.model.*
import com.mamonov.scoring.ScoreCalculator
import com.mamonov.specification.*

class MatchingService(
    private val calculator: ScoreCalculator = ScoreCalculator()
) {
    fun match(
        request: MatchingRequest,
        candidates: List<Center>,
        strategyType: String = "balanced"
    ): MatchingResult {

        // 1. Собираем фильтр из трёх спецификаций
        val spec = DistrictSpecification(request.district)
            .and(BudgetSpecification(request.maxBudget))
            .and(ServiceSpecification(request.requiredServices))

        // 2. Фильтруем кандидатов
        val filtered = candidates.filter { spec.isSatisfiedBy(it) }

        // 3. Считаем баллы
        val scored = filtered.map { center ->
            CenterScore(
                centerId = center.id,
                centerName = center.name,
                score = calculator.calculate(center, request)
            )
        }

        // 4. Сортируем по стратегии
        val strategy = StrategyFactory.create(strategyType)
        val ranked = strategy.rank(scored, filtered)

        return MatchingResult(centers = ranked)
    }
}