package ru.shalkoff.bus_schedule.plugins.route

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts.USER_PROFILE_ENDPOINT
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.generateHttpResponse
import ru.shalkoff.bus_schedule.controllers.auth.AuthCheckerController
import ru.shalkoff.bus_schedule.db.models.UserRole.Companion.mapToResponse

fun Application.configureRoutingProfile() {

    val authCheckerController by inject<AuthCheckerController>()

    routing {
        authenticate {
            get(USER_PROFILE_ENDPOINT) {
                try {
                    val user = authCheckerController.getUserByToken(call)
                    if (user != null) {
                        call.respond(user.mapToResponse())
                    } else {
                        throw Exception("Пользователь с таким токеном не найден")
                    }
                } catch (e: Exception) {
                    val response = generateHttpResponse(GeneralResponse.unauthorized(e.message))
                    call.respond(response.code, response.body)
                }
            }
        }
    }
}