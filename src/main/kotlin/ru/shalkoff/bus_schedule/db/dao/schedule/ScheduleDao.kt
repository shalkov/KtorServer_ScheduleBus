package ru.shalkoff.bus_schedule.db.dao.schedule

import ru.shalkoff.bus_schedule.db.models.ScheduleModel

interface ScheduleDao {

    suspend fun getAll(): List<ScheduleModel>
    suspend fun getByRouteNumber(routeNumber: String): ScheduleModel?

    suspend fun setupDefaultSchedule()
}