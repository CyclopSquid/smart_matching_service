package com.mamonov.strategy

import com.mamonov.model.Center
import com.mamonov.model.CenterScore

interface RankingStrategy {
    fun rank(scored: List<CenterScore>, centers: List<Center>): List<CenterScore>
}