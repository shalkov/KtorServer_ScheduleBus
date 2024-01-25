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
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DELETE_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DELETE_URL
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_NEW_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_NEW_URL
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_TIME_DELETE_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_TIME_DELETE_URL
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_TIME_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_TIME_URL
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_DEPARTURE_URL
import ru.shalkoff.bus_schedule.Consts.ADMIN_SCHEDULE_URL
import ru.shalkoff.bus_schedule.controllers.AdminController
import ru.shalkoff.bus_schedule.db.dao.schedule.ScheduleDao

fun Application.configureRoutingAdminSchedule() {

    val scheduleDao by inject<ScheduleDao>()
    val adminController by inject<AdminController>()

    suspend fun redirectToDeparture(call: ApplicationCall, departure: String, routeNumber: String) {
        val showDeparture = if (departure == "Start") { "showDepartureStart" } else { "showDepartureEnd" }
        val routeId = scheduleDao.getByRouteNumber(routeNumber)?.id ?: Exception("Не найден ID маршрута, по его номеру")
        call.respondRedirect(ADMIN_SCHEDULE_DEPARTURE_ENDPOINT + "?action=${showDeparture}&id=${routeId}")
    }

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
                                        "departureEndList" to scheduleDao.getAllDepartureEnd(),
                                        "scheduleUrl" to ADMIN_SCHEDULE_URL
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
                                        "departureEndList" to scheduleDao.getAllDepartureEnd(),
                                        "scheduleUrl" to ADMIN_SCHEDULE_URL
                                    )
                                )
                            )
                        }

                        else -> {
                            call.respond(
                                FreeMarkerContent(
                                    Consts.SCHEDULE_FTL,
                                    mapOf(
                                        "schedule" to scheduleDao.getAll().sortedBy { it.id },
                                        "departureUrl" to ADMIN_SCHEDULE_DEPARTURE_URL,
                                        "scheduleUrl" to ADMIN_SCHEDULE_URL,
                                        "scheduleDeleteUrl" to ADMIN_SCHEDULE_DELETE_URL,
                                        "departureNewUrl" to ADMIN_SCHEDULE_DEPARTURE_NEW_URL

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
                            val routeNumber =
                                postParameters["routeNumber"] ?: throw Exception("routeNumber param error")
                            val route = scheduleDao.getByRouteNumber(routeNumber)
                            if (route != null) {
                                throw BadRequestException("Маршрут с таким номером уже существует")
                            }
                            val name = postParameters["name"] ?: throw Exception("name param error")
                            val description =
                                postParameters["description"] ?: throw Exception("description param error")
                            val departureStart =
                                postParameters["departureStart"] ?: throw Exception("departureStart param error")
                            val departureEnd =
                                postParameters["departureEnd"] ?: throw Exception("departureEnd param error")
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
                            val routeNumber =
                                postParameters["routeNumber"] ?: throw Exception("routeNumber param error")
                            val name = postParameters["name"] ?: throw Exception("name param error")
                            val description =
                                postParameters["description"] ?: throw Exception("description param error")
                            val departureStart =
                                postParameters["departureStart"] ?: throw Exception("departureStart param error")
                            val departureEnd =
                                postParameters["departureEnd"] ?: throw Exception("departureEnd param error")
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

        route(ADMIN_SCHEDULE_DEPARTURE_ENDPOINT) {
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
                                "scheduleTimeList" to timeList,
                                "timeUrl" to ADMIN_SCHEDULE_DEPARTURE_TIME_URL,
                                "timeDeleteUrl" to ADMIN_SCHEDULE_DEPARTURE_TIME_DELETE_URL
                            )
                        )
                    )
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

        route(ADMIN_SCHEDULE_DEPARTURE_NEW_ENDPOINT) {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val nameDeparture =
                        call.request.queryParameters["nameDeparture"] ?: throw Exception("action param error")
                    call.respond(
                        FreeMarkerContent(
                            Consts.DEPARTURE_NEW_FTL,
                            mapOf("nameDeparture" to nameDeparture)
                        )
                    )
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
            post {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val postParameters: Parameters = call.receiveParameters()
                    val nameDeparture = postParameters["nameDeparture"] ?: throw Exception("nameDeparture param error")
                    val name = postParameters["name"] ?: throw Exception("name param error")
                    when (nameDeparture) {
                        "Start" -> {
                            scheduleDao.addDepartureStart(name)
                        }

                        "End" -> {
                            scheduleDao.addDepartureEnd(name)
                        }
                    }
                    call.respondRedirect(Consts.ADMIN_SCHEDULE_ENDPOINT)
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

        route(ADMIN_SCHEDULE_DEPARTURE_TIME_ENDPOINT) {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val action = call.request.queryParameters["action"] ?: throw Exception("action param error")
                    when (action) {
                        "new" -> {
                            val routeNumber = call.request.queryParameters["routeNumber"]
                                ?: throw Exception("routeNumber param error")
                            val departure =
                                call.request.queryParameters["departure"] ?: throw Exception("departure param error")
                            val departureName = call.request.queryParameters["departureName"]
                                ?: throw Exception("departureName param error")

                            call.respond(
                                FreeMarkerContent(
                                    Consts.TIME_NEW_EDIT_FTL,
                                    mapOf(
                                        "routeNumber" to routeNumber,
                                        "departure" to departure,
                                        "departureName" to departureName,
                                        "action" to action,

                                        )
                                )
                            )
                        }

                        "edit" -> {
                            val routeNumber = call.request.queryParameters["routeNumber"]
                                ?: throw Exception("routeNumber param error")
                            val departure =
                                call.request.queryParameters["departure"] ?: throw Exception("departure param error")
                            val departureName = call.request.queryParameters["departureName"]
                                ?: throw Exception("departureName param error")
                            val timeId = call.request.queryParameters["timeId"] ?: throw Exception("id param error")
                            val route = scheduleDao.getByRouteNumber(routeNumber)
                                ?: throw BadRequestException("Такого маршрута не существует")

                            val departureInfo = if (departure == "Start") {
                                route.departureStart
                            } else {
                                route.departureEnd
                            }
                            val timeSchedule = departureInfo.timeList.firstOrNull { it.id == timeId.toInt() }
                                ?: throw Exception("timeId.toInt() error")

                            call.respond(
                                FreeMarkerContent(
                                    Consts.TIME_NEW_EDIT_FTL,
                                    mapOf(
                                        "routeNumber" to routeNumber,
                                        "departure" to departure,
                                        "departureName" to departureName,
                                        "action" to action,
                                        "scheduleTime" to timeSchedule
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
                    val action = postParameters["action"] ?: throw Exception("action param error (only 'new' or 'edit' value)")
                    val routeNumber =
                        postParameters["routeNumber"] ?: throw Exception("routeNumber param error")
                    val time = postParameters["time"] ?: throw Exception("time param error")
                    val description =
                        postParameters["description"] ?: throw Exception("description param error")
                    val departure = postParameters["departure"] ?: throw Exception("departure param error")

                    when (action) {
                        "new" -> {
                            if (departure == "Start") {
                                scheduleDao.addTimeDepartureStart(
                                    routeNumber,
                                    time,
                                    description
                                )
                            }
                            if (departure == "End") {
                                scheduleDao.addTimeDepartureEnd(
                                    routeNumber,
                                    time,
                                    description
                                )
                            }
                        }

                        "edit" -> {
                            val id = postParameters["id"]?.toInt() ?: throw Exception("id param error")
                            if (departure == "Start") {
                                scheduleDao.editTimeDepartureStart(
                                    id,
                                    routeNumber,
                                    time,
                                    description
                                )
                            }
                            if (departure == "End") {
                                scheduleDao.editTimeDepartureEnd(
                                    id,
                                    routeNumber,
                                    time,
                                    description
                                )
                            }
                        }
                    }
                    redirectToDeparture(call, departure, routeNumber)
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

        route(ADMIN_SCHEDULE_DELETE_ENDPOINT) {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val id = call.request.queryParameters["id"]?.toInt() ?: throw Exception("id param error")
                    scheduleDao.deleteRoute(id)
                    call.respondRedirect(Consts.ADMIN_SCHEDULE_ENDPOINT)
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

        route(ADMIN_SCHEDULE_DEPARTURE_TIME_DELETE_ENDPOINT) {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val routeNumber = call.request.queryParameters["routeNumber"] ?: throw Exception("routeNumber param error")
                    val departure = call.request.queryParameters["departure"] ?: throw Exception("departure param error")
                    val timeId = call.request.queryParameters["timeId"]?.toInt() ?: throw Exception("timeId param error")
                    if (departure == "Start") {
                        scheduleDao.deleteTimeStart(timeId)
                    }
                    if (departure == "End") {
                        scheduleDao.deleteTimeEnd(timeId)
                    }

                    redirectToDeparture(call, departure, routeNumber)
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }
    }
}