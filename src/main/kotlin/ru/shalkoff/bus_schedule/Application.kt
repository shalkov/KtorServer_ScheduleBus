package ru.shalkoff.bus_schedule

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.db.DbFactory
import ru.shalkoff.bus_schedule.plugins.*
import ru.shalkoff.bus_schedule.plugins.route.configureRouting
import ru.shalkoff.bus_schedule.plugins.route.configureRoutingAdmin
import ru.shalkoff.bus_schedule.plugins.route.configureRoutingSchedule

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ) .start(wait = true)
}

fun Application.module() {
    val jwtHelper by inject<JWTHelper>()

    DbFactory.init()
    configureSerialization()
    configureKoin()
    configureSwaggerUI()
    configureSecurity(jwtHelper)
    configureFreemarker()
    configureRouting()
    configureRoutingAdmin()
    configureRoutingSchedule()
}