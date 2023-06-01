package ru.shalkoff.bus_schedule.plugins.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.base.generateHttpResponse
import ru.shalkoff.bus_schedule.controllers.ScheduleController

/**
 * Маршруты для запросов связанные с расписанием
 */
fun Application.configureRoutingSchedule() {

    val scheduleController by inject<ScheduleController>()

    routing {

        get("schedule/all") {
            val scheduleResponse = scheduleController.getAll()
            val response = generateHttpResponse(scheduleResponse)

            call.respond(response.code, response.body)
        }

        get("schedule/{route_number}") {
            val routeNumber = call.parameters["route_number"]
            val scheduleResponse = scheduleController.getRouteByNumber(routeNumber)
            val response = generateHttpResponse(scheduleResponse)

            call.respond(response.code, response.body)
        }

    }

}