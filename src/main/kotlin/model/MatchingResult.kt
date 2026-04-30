package com.mamonov.model

import kotlinx.serialization.Serializable

@Serializable
data class MatchingResult(
    val centers: List<CenterScore>
)