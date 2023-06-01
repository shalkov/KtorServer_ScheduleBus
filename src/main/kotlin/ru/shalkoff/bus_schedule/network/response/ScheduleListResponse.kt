package ru.shalkoff.bus_schedule.network.response

import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.InfoResponse
import ru.shalkoff.bus_schedule.db.models.ScheduleModel

data class ScheduleListResponse(
    val routes: List<ScheduleModel>,
    override val info: InfoResponse
) : BaseResponse