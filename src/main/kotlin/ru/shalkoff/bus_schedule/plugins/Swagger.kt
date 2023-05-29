package ru.shalkoff.bus_schedule.plugins

import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureSwaggerUI() {

    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
    }
}
