package ru.shalkoff.bus_schedule.plugins.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.generateHttpResponse
import ru.shalkoff.bus_schedule.controllers.auth.AuthCheckerController

fun Application.configureRoutingProfile() {

    val authCheckerController by inject<AuthCheckerController>()

    routing {
        authenticate {
            get("user/profile") {
                val user = authCheckerController.getUserByToken(call)
                if (user != null) {
                    call.respond(user)
                } else {
                    val response = generateHttpResponse(GeneralResponse.unauthorized("Вы не авторизованы"))
                    call.respond(response.code, response.body)
                }
            }
        }
    }
}