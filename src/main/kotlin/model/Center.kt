package com.mamonov.model

import kotlinx.serialization.Serializable

@Serializable
data class Center(
    val id: Int,
    val name: String,
    val district: String,
    val pricePerDay: Int,
    val services: List<String>,
    val rating: Double,
    val distanceKm: Double = 0.0
)