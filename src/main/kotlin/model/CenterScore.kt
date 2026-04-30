package com.mamonov.model

import kotlinx.serialization.Serializable

@Serializable
data class CenterScore(
    val centerId: Int,
    val centerName: String,
    val score: Int
)