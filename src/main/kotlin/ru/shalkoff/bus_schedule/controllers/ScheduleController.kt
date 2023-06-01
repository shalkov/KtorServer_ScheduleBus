package ru.shalkoff.bus_schedule.controllers

import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.InfoResponse
import ru.shalkoff.bus_schedule.base.State
import ru.shalkoff.bus_schedule.db.dao.schedule.ScheduleDao
import ru.shalkoff.bus_schedule.network.response.ScheduleListResponse
import ru.shalkoff.bus_schedule.network.response.ScheduleResponse

class ScheduleController : KoinComponent {

    private val scheduleDao by inject<ScheduleDao>()

    suspend fun getAll(): BaseResponse {

        return try {
            val allRoutes = scheduleDao.getAll()
            ScheduleListResponse(
                allRoutes,
                InfoResponse(
                    status = State.SUCCESS,
                    message = "Список всех маршрутов успешно предоставлен!"
                )
            )
        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }

    suspend fun getRouteByNumber(routeNumber: String?): BaseResponse {
        return try {
            if (routeNumber == null) {
                throw NullPointerException()
            }
            val scheduleModel = scheduleDao.getByRouteNumber(routeNumber)
                ?: throw BadRequestException("Нет информации об этом маршруте")

            ScheduleResponse(
                scheduleModel,
                InfoResponse(
                    status = State.SUCCESS,
                    message = "Маршрут успешно предоставлен!"
                )
            )
        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }
}