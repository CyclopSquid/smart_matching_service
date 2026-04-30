package com.mamonov.model

import kotlinx.serialization.Serializable

@Serializable
data class MatchingRequest(
    val district: String,
    val maxBudget: Int,
    val requiredServices: List<String>,
    val preferredRating: Double
)