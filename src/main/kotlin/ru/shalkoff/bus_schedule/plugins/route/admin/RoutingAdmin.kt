package ru.shalkoff.bus_schedule.plugins.route.admin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_URL
import ru.shalkoff.bus_schedule.Consts.ADMIN_USER_ALL_URL
import ru.shalkoff.bus_schedule.Consts.AUTH_ENDPOINT
import ru.shalkoff.bus_schedule.controllers.AdminController

fun Application.configureRoutingAdmin() {

    val adminController by inject<AdminController>()

    routing {
        get(Consts.ADMIN_ENDPOINT) {
            try {
                adminController.checkUserAccessAdminPanel(call)
                val errorText = call.request.queryParameters["errorText"]
                call.respond(
                    FreeMarkerContent(
                        Consts.INDEX_FTL,
                        mapOf(
                            "error_alert" to errorText,
                            "userAllUrl" to ADMIN_USER_ALL_URL,
                            "scheduleUrl" to ADMIN_SCHEDULE_URL
                        )
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    FreeMarkerContent(
                        Consts.LOGIN_FTL,
                        mapOf(
                            "error" to e.message,
                            "authUrl" to AUTH_ENDPOINT
                            )
                    )
                )
            }
        }
    }
}