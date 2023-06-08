package ru.shalkoff.bus_schedule.db.dao.schedule

import ru.shalkoff.bus_schedule.db.models.ScheduleModel

interface ScheduleDao {

    suspend fun getAll(): List<ScheduleModel>
    suspend fun getByRouteNumber(routeNumber: String): ScheduleModel?
    suspend fun getByRouteId(routeId: Int): ScheduleModel?

    suspend fun getAllDepartureStart(): List<String>
    suspend fun getAllDepartureEnd(): List<String>

    suspend fun addRoute(
        routeNumber: String,
        name: String,
        description: String,
        departureStart: String,
        departureEnd: String
    ): Boolean

    suspend fun editRoute(
        id: Int,
        routeNumber: String,
        name: String,
        description: String,
        departureStart: String,
        departureEnd: String
    ): Boolean

    suspend fun setupDefaultSchedule()
}