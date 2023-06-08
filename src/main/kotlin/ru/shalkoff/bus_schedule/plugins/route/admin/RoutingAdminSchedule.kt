package ru.shalkoff.bus_schedule.plugins.route.admin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
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
        route(Consts.ADMIN_SCHEDULE_ENDPOINT) {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val action = call.request.queryParameters["action"]
                    when (action) {
                        "new" -> {
                            call.respond(
                                FreeMarkerContent(
                                    Consts.SCHEDULE_NEW_EDIT_FTL,
                                    mapOf(
                                        "action" to action,
                                        "departureStartList" to scheduleDao.getAllDepartureStart(),
                                        "departureEndList" to scheduleDao.getAllDepartureEnd()
                                    )
                                )
                            )
                        }

                        "edit" -> {
                            val id = call.request.queryParameters["id"] ?: throw Exception("id param error")
                            val route = scheduleDao.getByRouteId(id.toInt()) ?: throw Exception("route error")
                            call.respond(
                                FreeMarkerContent(
                                    Consts.SCHEDULE_NEW_EDIT_FTL,
                                    mapOf(
                                        "action" to action,
                                        "route" to route,
                                        "departureStartList" to scheduleDao.getAllDepartureStart(),
                                        "departureEndList" to scheduleDao.getAllDepartureEnd()
                                    )
                                )
                            )
                        }

                        else -> {
                            call.respond(
                                FreeMarkerContent(
                                    Consts.SCHEDULE_FTL,
                                    mapOf(
                                        "schedule" to scheduleDao.getAll().sortedBy { it.id }
                                    )
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
            post {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val postParameters: Parameters = call.receiveParameters()
                    val action = postParameters["action"] ?: "new"
                    when (action) {
                        "new" -> {
                            val routeNumber = postParameters["routeNumber"] ?: throw Exception("routeNumber param error")
                            val route = scheduleDao.getByRouteNumber(routeNumber)
                            if (route != null) {
                                throw BadRequestException("Маршрут с таким номером уже существует")
                            }
                            val name = postParameters["name"] ?: throw Exception("name param error")
                            val description = postParameters["description"] ?: throw Exception("description param error")
                            val departureStart = postParameters["departureStart"] ?: throw Exception("departureStart param error")
                            val departureEnd = postParameters["departureEnd"] ?: throw Exception("departureEnd param error")
                            scheduleDao.addRoute(
                                routeNumber,
                                name,
                                description,
                                departureStart,
                                departureEnd
                            )
                        }
                        "edit" -> {
                            val id = postParameters["id"]?.toInt() ?: throw Exception("id param error")
                            val routeNumber = postParameters["routeNumber"] ?: throw Exception("routeNumber param error")
                            val name = postParameters["name"] ?: throw Exception("name param error")
                            val description = postParameters["description"] ?: throw Exception("description param error")
                            val departureStart = postParameters["departureStart"] ?: throw Exception("departureStart param error")
                            val departureEnd = postParameters["departureEnd"] ?: throw Exception("departureEnd param error")
                            scheduleDao.editRoute(
                                id,
                                routeNumber,
                                name,
                                description,
                                departureStart,
                                departureEnd
                            )
                        }
                    }
                    call.respondRedirect(Consts.ADMIN_SCHEDULE_ENDPOINT)
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

        route("admin/schedule/departure") {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val action = call.request.queryParameters["action"] ?: throw Exception("action param error")
                    val departure = if (action == "showDepartureStart") {
                        "Start"
                    } else {
                        "End"
                    }
                    val id = call.request.queryParameters["id"] ?: throw Exception("id param error")
                    val route = scheduleDao.getByRouteId(id.toInt()) ?: throw Exception("route error")
                    val departureFrom = if (action == "showDepartureStart") {
                        route.departureStart.departureFrom
                    } else {
                        route.departureEnd.departureFrom
                    }
                    val timeList = if (action == "showDepartureStart") {
                        route.departureStart.timeList
                    } else {
                        route.departureEnd.timeList
                    }
                    call.respond(
                        FreeMarkerContent(
                            Consts.DEPARTURE_FTL,
                            mapOf(
                                "routeNumber" to route.routeNumber,
                                "departure" to departure,
                                "departureName" to departureFrom,
                                "scheduleTimeList" to timeList
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