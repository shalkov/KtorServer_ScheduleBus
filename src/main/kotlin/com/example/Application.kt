package com.example

import com.example.auth.JWTHelper
import com.example.db.DbFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import org.koin.ktor.ext.inject

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
    //configureHTTP()
    configureSecurity(jwtHelper)
    configureRouting()
}
