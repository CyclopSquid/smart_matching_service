package com.mamonov

import com.mamonov.model.Center
import com.mamonov.model.MatchingRequest
import com.mamonov.scoring.ScoreCalculator
import com.mamonov.service.MatchingService
import com.mamonov.specification.BudgetSpecification
import com.mamonov.specification.DistrictSpecification
import com.mamonov.specification.ServiceSpecification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ApplicationTest {

    private val centers = listOf(
        Center(1, "Лапки", "Центральный", 3000, listOf("Вакцинация", "Стрижка"), 4.8, 1.2),
        Center(2, "Барбос", "Северный", 2000, listOf("Стрижка"), 4.1, 3.5),
        Center(3, "Мурка", "Центральный", 6000, listOf("Вакцинация"), 4.5, 2.1),
        Center(4, "Хвост", "Центральный", 4500, listOf("Вакцинация", "Стрижка"), 3.8, 0.8)
    )

    // MatchingService тесты
    @Test
    fun returnsOnlyCentersFromRequestedDistrict() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers)
        assertTrue(result.centers.all { scored ->
            centers.find { it.id == scored.centerId }?.district == "Центральный"
        })
    }

    @Test
    fun doesNotReturnCentersOverBudget() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers)
        assertTrue(result.centers.none { it.centerId == 3 })
    }

    @Test
    fun returnsEmptyResultWhenNoCentersMatch() {
        val request = MatchingRequest("Южный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers)
        assertTrue(result.centers.isEmpty())
    }

    @Test
    fun resultsAreSortedByScoreDescendingForBalancedStrategy() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация", "Стрижка"), 4.0)
        val result = MatchingService().match(request, centers, "balanced")
        val scores = result.centers.map { it.score }
        assertEquals(scores.sortedDescending(), scores)
    }

    @Test
    fun cheapestStrategyReturnsCheapestFirst() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers, "cheapest")
        val prices = result.centers.map { scored ->
            centers.find { it.id == scored.centerId }?.pricePerDay ?: 0
        }
        assertEquals(prices.sorted(), prices)
    }

    @Test
    fun ratingStrategyReturnsBestRatingFirst() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers, "rating")
        val ratings = result.centers.map { scored ->
            centers.find { it.id == scored.centerId }?.rating ?: 0.0
        }
        assertEquals(ratings.sortedDescending(), ratings)
    }

    @Test
    fun closestStrategyReturnsClosestFirst() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers, "closest")
        val distances = result.centers.map { scored ->
            centers.find { it.id == scored.centerId }?.distanceKm ?: 0.0
        }
        assertEquals(distances.sorted(), distances)
    }

    // ScoreCalculator тесты
    @Test
    fun scoreIsHundredForPerfectMatch() {
        val calc = ScoreCalculator()
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Вакцинация"), 4.8, 1.0)
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.5)
        assertEquals(100, calc.calculate(center, request))
    }

    @Test
    fun scoreIsZeroWhenNothingMatches() {
        val calc = ScoreCalculator()
        val center = Center(1, "Тест", "Северный", 6000, listOf("Груминг"), 3.0, 1.0)
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.5)
        assertEquals(0, calc.calculate(center, request))
    }

    @Test
    fun scoreIncludesOnlyDistrictAndBudgetWhenNoServiceMatch() {
        val calc = ScoreCalculator()
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Груминг"), 4.8, 1.0)
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.5)
        assertEquals(55, calc.calculate(center, request)) // 30 + 25
    }

    // Specification тесты
    @Test
    fun districtSpecificationAcceptsMatchingCenter() {
        val spec = DistrictSpecification("Центральный")
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Вакцинация"), 4.8, 1.0)
        assertTrue(spec.isSatisfiedBy(center))
    }

    @Test
    fun districtSpecificationRejectsNonMatchingCenter() {
        val spec = DistrictSpecification("Центральный")
        val center = Center(1, "Тест", "Северный", 3000, listOf("Вакцинация"), 4.8, 1.0)
        assertTrue(!spec.isSatisfiedBy(center))
    }

    @Test
    fun budgetSpecificationAcceptsCenterWithinBudget() {
        val spec = BudgetSpecification(5000)
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Вакцинация"), 4.8, 1.0)
        assertTrue(spec.isSatisfiedBy(center))
    }

    @Test
    fun budgetSpecificationRejectsCenterOverBudget() {
        val spec = BudgetSpecification(5000)
        val center = Center(1, "Тест", "Центральный", 6000, listOf("Вакцинация"), 4.8, 1.0)
        assertTrue(!spec.isSatisfiedBy(center))
    }

    @Test
    fun serviceSpecificationAcceptsCenterWithRequiredService() {
        val spec = ServiceSpecification(listOf("Вакцинация"))
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Вакцинация", "Стрижка"), 4.8, 1.0)
        assertTrue(spec.isSatisfiedBy(center))
    }

    @Test
    fun serviceSpecificationRejectsCenterWithoutRequiredService() {
        val spec = ServiceSpecification(listOf("Вакцинация"))
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Стрижка"), 4.8, 1.0)
        assertTrue(!spec.isSatisfiedBy(center))
    }
}