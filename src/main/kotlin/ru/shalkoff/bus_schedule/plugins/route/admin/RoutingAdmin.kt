package ru.shalkoff.bus_schedule.plugins.route.admin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.controllers.AdminController

fun Application.configureRoutingAdmin() {

    val adminController by inject<AdminController>()

    routing {
        get(Consts.ADMIN_ENDPOINT) {
            try {
                adminController.checkUserAccessAdminPanel(call)
                call.respond(
                    FreeMarkerContent(
                        Consts.INDEX_FTL, null
//                        mapOf(
//                            "error_alert" to adminController.alertError
//                        )
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    FreeMarkerContent(Consts.LOGIN_FTL, mapOf("error" to e.message))
                )
            }
        }
    }
}