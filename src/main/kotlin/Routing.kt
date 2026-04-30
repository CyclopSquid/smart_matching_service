package com.mamonov

import com.mamonov.model.MatchingRequest
import com.mamonov.service.MatchingService
import com.mamonov.service.sampleCenters
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.*

fun Application.configureRouting() {
    val service = MatchingService()

    routing {
        post("/match") {
            val request = call.receive<MatchingRequest>()
            val strategy = call.request.queryParameters["strategy"] ?: "balanced"
            val result = service.match(request, sampleCenters, strategy)
            call.respond(result)
        }
    }
}
