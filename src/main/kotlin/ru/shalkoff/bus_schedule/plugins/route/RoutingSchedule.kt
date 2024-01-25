package ru.shalkoff.bus_schedule.plugins.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts.SCHEDULE_ALL_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.SCHEDULE_ROUTE_NUMBER_ENDPOINT
import ru.shalkoff.bus_schedule.base.generateHttpResponse
import ru.shalkoff.bus_schedule.controllers.ScheduleController

/**
 * Маршруты для запросов связанные с расписанием
 */
fun Application.configureRoutingSchedule() {

    val scheduleController by inject<ScheduleController>()

    routing {

        get(SCHEDULE_ALL_ENDPOINT) {
            val scheduleResponse = scheduleController.getAll()
            val response = generateHttpResponse(scheduleResponse)

            call.respond(response.code, response.body)
        }

        get(SCHEDULE_ROUTE_NUMBER_ENDPOINT) {
            val routeNumber = call.parameters["route_number"]
            val scheduleResponse = scheduleController.getRouteByNumber(routeNumber)
            val response = generateHttpResponse(scheduleResponse)

            call.respond(response.code, response.body)
        }

    }

}