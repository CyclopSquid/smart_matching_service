package com.mamonov

import com.mamonov.model.Center
import com.mamonov.model.MatchingRequest
import com.mamonov.scoring.ScoreCalculator
import com.mamonov.service.MatchingService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class ApplicationTest {

    private val centers = listOf(
        Center(1, "Лапки", "Центральный", 3000, listOf("Вакцинация", "Стрижка"), 4.8),
        Center(2, "Барбос", "Северный", 2000, listOf("Стрижка"), 4.1),
        Center(3, "Мурка", "Центральный", 6000, listOf("Вакцинация"), 4.5)
    )

    @Test
    fun returnsOnlyCentersFromRequestedDistrict() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers)
        assertEquals(1, result.centers.size)
        assertEquals(1, result.centers[0].centerId)
    }

    @Test
    fun doesNotReturnCentersOverBudget() {
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers)
        assertTrue(result.centers.none { it.centerId == 3 })
    }

    @Test
    fun scoreIsHundredForPerfectMatch() {
        val calc = ScoreCalculator()
        val center = Center(1, "Тест", "Центральный", 3000, listOf("Вакцинация"), 4.8)
        val request = MatchingRequest("Центральный", 5000, listOf("Вакцинация"), 4.5)
        assertEquals(100, calc.calculate(center, request))
    }

    @Test
    fun returnsEmptyResultWhenNoCentersMatch() {
        val request = MatchingRequest("Южный", 5000, listOf("Вакцинация"), 4.0)
        val result = MatchingService().match(request, centers)
        assertTrue(result.centers.isEmpty())
    }
}