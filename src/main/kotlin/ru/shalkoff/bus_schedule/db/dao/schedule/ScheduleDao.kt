package ru.shalkoff.bus_schedule.db.dao.schedule

import ru.shalkoff.bus_schedule.db.models.ScheduleModel

interface ScheduleDao {

    suspend fun setupDefaultSchedule()

    suspend fun getAll(): List<ScheduleModel>
    suspend fun getByRouteNumber(routeNumber: String): ScheduleModel?
}