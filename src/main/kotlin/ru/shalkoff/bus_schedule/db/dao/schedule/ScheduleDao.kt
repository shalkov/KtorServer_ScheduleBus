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
    suspend fun addDepartureStart(name: String)
    suspend fun addDepartureEnd(name: String)

    suspend fun addTimeDepartureStart(
        routeNumber: String,
        time: String,
        description: String
    ): Boolean

    suspend fun addTimeDepartureEnd(
        routeNumber: String,
        time: String,
        description: String
    ): Boolean

    suspend fun editTimeDepartureStart(
        id: Int,
        routeNumber: String,
        time: String,
        description: String
    ): Boolean

    suspend fun editTimeDepartureEnd(
        id: Int,
        routeNumber: String,
        time: String,
        description: String
    ): Boolean

    suspend fun deleteRoute(id: Int): Boolean
    suspend fun deleteTimeStart(timeId: Int): Boolean

    suspend fun deleteTimeEnd(timeId: Int): Boolean
}