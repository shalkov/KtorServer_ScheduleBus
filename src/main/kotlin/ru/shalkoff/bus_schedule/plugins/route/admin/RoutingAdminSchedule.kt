package ru.shalkoff.bus_schedule.plugins.route.admin

import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.controllers.AdminController
import ru.shalkoff.bus_schedule.db.dao.schedule.ScheduleDao

fun Application.configureRoutingAdminSchedule() {

    val scheduleDao by inject<ScheduleDao>()
    val adminController by inject<AdminController>()

    routing {
        route("admin/schedule") {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    call.respond(
                        FreeMarkerContent(
                            Consts.SCHEDULE_FTL,
                            mapOf(
                                "schedule" to scheduleDao.getAll().sortedBy { it.id }
                            )
                        )
                    )
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

    }
}