package ru.shalkoff.bus_schedule.db.dao.schedule

import ru.shalkoff.bus_schedule.db.DbFactory
import ru.shalkoff.bus_schedule.db.models.DepartureInfo
import ru.shalkoff.bus_schedule.db.models.ScheduleModel
import ru.shalkoff.bus_schedule.db.models.ScheduleTime
import ru.shalkoff.bus_schedule.db.tables.*

class ScheduleDaoImpl : ScheduleDao {

    override suspend fun getAll(): List<ScheduleModel> {
        return DbFactory.dbQuery {
            val allNumberRoute = EntityRouteNumber.all().toList()

            val allRoutes = mutableListOf<ScheduleModel>()
            allNumberRoute.forEach {
                val scheduleModel = getByRouteNumber(it.number)
                if (scheduleModel != null) {
                    allRoutes.add(scheduleModel)
                }
            }
            allRoutes
        }
    }

    override suspend fun getByRouteNumber(routeNumber: String): ScheduleModel? {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
            createScheduleModel(route)
        }
    }

    override suspend fun getByRouteId(routeId: Int): ScheduleModel? {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.id eq routeId }.firstOrNull()
            createScheduleModel(route)
        }
    }

    override suspend fun getAllDepartureStart(): List<String> {
        return DbFactory.dbQuery {
            EntityDepartureStart.all().map { it.title }
        }
    }

    override suspend fun getAllDepartureEnd(): List<String> {
        return DbFactory.dbQuery {
            EntityDepartureEnd.all().map { it.title }
        }
    }

    override suspend fun addRoute(
        routeNumber: String,
        name: String,
        description: String,
        departureStart: String,
        departureEnd: String
    ): Boolean {
        return DbFactory.dbQuery {
            val routeNumberEntity = createOrGetEntityRouteNumber(routeNumber)
            val departure = getDeparture(departureStart, departureEnd)

            EntityRoute.new {
                this.routeNumber = routeNumberEntity
                this.title = name
                this.description = description
                this.departureTitleStart = departure.first
                this.departureTitleEnd = departure.second
            }
            true
        }
    }

    override suspend fun editRoute(
        id: Int,
        routeNumber: String,
        name: String,
        description: String,
        departureStart: String,
        departureEnd: String
    ): Boolean {
        return DbFactory.dbQuery {
            val routeNumberEntity = if (EntityRoute[id].routeNumber.number == routeNumber) {
                // значит при изменении, не поменяли номер маршрта
                EntityRoute[id].routeNumber
            } else {
                createOrGetEntityRouteNumber(routeNumber)
            }
            val departure = getDeparture(departureStart, departureEnd)

            EntityRoute[id].apply {
                this.routeNumber = routeNumberEntity
                this.title = name
                this.description = description
                this.departureTitleStart = departure.first
                this.departureTitleEnd = departure.second
            }
            true
        }
    }

    override suspend fun addDepartureStart(name: String) {
        DbFactory.dbQuery {
            EntityDepartureStart.new {
                title = name
            }
        }
    }

    override suspend fun addDepartureEnd(name: String) {
        DbFactory.dbQuery {
            EntityDepartureEnd.new {
                title = name
            }
        }
    }

    override suspend fun addTimeDepartureStart(
        routeNumber:
        String, time: String,
        description: String
    ): Boolean {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
                ?: throw Exception("Не найден маршрут с таким номером")
            EntityTimeStart.new {
                this.route = route
                this.time = time
                this.description = description
            }
            true
        }
    }

    override suspend fun addTimeDepartureEnd(
        routeNumber:
        String, time: String,
        description: String
    ): Boolean {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
                ?: throw Exception("Не найден маршрут с таким номером")
            EntityTimeEnd.new {
                this.route = route
                this.time = time
                this.description = description
            }
            true
        }
    }

    override suspend fun editTimeDepartureStart(
        id: Int,
        routeNumber: String,
        time: String,
        description: String
    ): Boolean {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
                ?: throw Exception("Не найден маршрут с таким номером")
            EntityTimeStart[id].apply {
                this.route = route
                this.time = time
                this.description = description
            }
            true
        }
    }

    override suspend fun editTimeDepartureEnd(
        id: Int,
        routeNumber: String,
        time: String,
        description: String
    ): Boolean {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
                ?: throw Exception("Не найден маршрут с таким номером")
            EntityTimeEnd[id].apply {
                this.route = route
                this.time = time
                this.description = description
            }
            true
        }
    }

    override suspend fun deleteRoute(id: Int): Boolean {
        return DbFactory.dbQuery {
            val route = EntityRoute.find { Routes.id eq id }.firstOrNull()
                ?: throw Exception("Не найден маршрут с таким ID")
            // удаляем все времена отправления из начальной точки, привязанные к этому маршруту
            val timeStartList = EntityTimeStart.find { TimesStart.routeId eq route.id }
            timeStartList.forEach { it.delete() }

            // удаляем все времена отправления из конечной точки, привязанные к этому маршруту
            val timeEndList = EntityTimeEnd.find { TimesEnd.routeId eq route.id }
            timeEndList.forEach { it.delete() }

            //удаляем сам маршрту.
            route.delete()
            true
        }
    }

    override suspend fun deleteTimeStart(timeId: Int): Boolean {
        return DbFactory.dbQuery {
//            val route = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
//                ?: throw Exception("Не найден маршрут с таким номером")
            val entityTime = EntityTimeStart.find { TimesStart.id eq timeId }.firstOrNull() ?: throw Exception("Не найден ID времени")
            entityTime.delete()
            true
        }
    }

    override suspend fun deleteTimeEnd(timeId: Int): Boolean {
        return DbFactory.dbQuery {
            val entityTime = EntityTimeEnd.find { TimesEnd.id eq timeId }.firstOrNull() ?: throw Exception("Не найден ID времени")
            entityTime.delete()
            true
        }
    }

    private suspend fun createOrGetEntityRouteNumber(routeNumber: String): EntityRouteNumber {
        return DbFactory.dbQuery {
            // Надо сначала проверить, не занят ли уже этот номер маршрута дргими
            val routeHasNumber = EntityRoute.find { Routes.number eq routeNumber }.firstOrNull()
            if (routeHasNumber != null) {
                throw Exception("Этот номер маршрута: $routeNumber уже используется другим расписанием, он должен быть уникальным")
            } else {
                // такого номера маршрута в таблице route нет.
                // теперь надо посмотреть в таблице RouteNumbers, если там есть берём оттуда, если нет, то создаём.
                EntityRouteNumber.find {
                    RouteNumbers.number eq routeNumber
                }.firstOrNull()
                    ?: EntityRouteNumber.new {
                        number = routeNumber
                    }
            }
        }
    }

    private suspend fun getDeparture(
        departureStart: String,
        departureEnd: String
    ): Pair<EntityDepartureStart, EntityDepartureEnd> {
        return DbFactory.dbQuery {
            val departureStartEntity = EntityDepartureStart.find {
                DeparturesStart.title eq departureStart
            }.firstOrNull()
                ?: throw Exception("Отправления(из начальной точки) под названием $departureStart не существует, добавьте его сначала")
            val departureEndEntity = EntityDepartureEnd.find {
                DeparturesEnd.title eq departureEnd
            }.firstOrNull()
                ?: throw Exception("Отправления(из конечной точки) под названием $departureEnd не существует, добавьте его сначала")

            departureStartEntity to departureEndEntity
        }
    }

    private fun createScheduleModel(route: EntityRoute?): ScheduleModel? {
        var scheduleModel: ScheduleModel? = null
        if (route != null) {
            val startTimes = EntityTimeStart.find { TimesStart.routeId eq route.id.value }.toList()
            val endTimes = EntityTimeEnd.find { TimesEnd.routeId eq route.id.value }.toList()
            scheduleModel = ScheduleModel(
                route.id.value,
                route.routeNumber.number,
                route.title,
                route.description,
                DepartureInfo(
                    route.departureTitleStart.id.value,
                    route.departureTitleStart.title,
                    startTimes.map { ScheduleTime(it.id.value, it.time, it.description) }
                ),
                DepartureInfo(
                    route.departureTitleEnd.id.value,
                    route.departureTitleEnd.title,
                    endTimes.map { ScheduleTime(it.id.value, it.time, it.description) }
                )
            )
        }
        return scheduleModel
    }

    /**
     * Заполнить таблицы информацией об расписании автобусов
     */
    override suspend fun setupDefaultSchedule() {
        return DbFactory.dbQuery {

            val timeList_310_graphskoe = listOf(
                "6:05",
                "6:20",
                "8:00",
                "8:50",
                "9:40",
                "10:25",
                "11:15",
                "12:10",
                "13:00",
                "13:50",
                "14:35",
                "15:20",
                "16:10",
                "16:55",
                "17:45",
                "18:30",
                "19:20",
                "20:00"
            )
            val timeList_310_voronezh = listOf(
                "6:50",
                "7:40",
                "8:30",
                "9:15",
                "10:05",
                "11:00",
                "11:50",
                "12:40",
                "13:25",
                "14:10",
                "15:00",
                "15:40",
                "16:35",
                "17:20",
                "18:15",
                "18:50",
                "19:30",
                "20:10",
                "21:00"
            )

            val timeList_371_orlovo = listOf(
                "6:00",
                "6:30",
                "7:00",
                "7:30",
                ":00",
                "8:30",
                "9:05",
                "9:40",
                "10:15",
                "10:50",
                "11:25",
                "12:00",
                "12:35",
                "13:10",
                "13:45",
                "14:20",
                "14:55",
                "15:30",
                "16:15",
                "17:00",
                "17:45",
                "18:30",
                "19:15",
                "20:00"
            )

            val timeList_371_voronezh = listOf(
                "6:50",
                "7:30",
                "8:00",
                "8:30",
                "9:00",
                "9:30",
                "10:00",
                "10:35",
                "11:10",
                "11:45",
                "12:20",
                "12:55",
                "13:30",
                "14:35",
                "15:10",
                "15:45",
                "16:20",
                "16:55",
                "17:30",
                "18:10",
                "18:50",
                "19:30",
                "20:10",
                "20:50",
                "21:30"
            )

            val timeList_301_uglyanec = listOf("6:05", "6:50", "9:15", "10:20", "13:00", "16:10")
            val timeList_301_voronezh = listOf("7:50", "11:35", "14:35")
            val timeList_302_uglyanec = listOf("8:05", "11:30", "14:00", "15:00", "16:50")
            val timeList_302_voronezh =
                listOf("7:10", "9:00", "10:15", "12:25", "13:40", "15:30", "16:30", "18:00", "18:30")
            val timeList_312_volya = listOf("6:30", "11:30", "15:15")
            val timeList_312_novaya_usman = listOf("10:45", "14:45")

            val nameRouteVoronezh = EntityDepartureEnd.new { title = "Воронеж" }
            val nameRouteNovayaUsman = EntityDepartureEnd.new { title = "Новая Усмань" }

            val nameRouteOrlovo = EntityDepartureStart.new { title = "Орлово" }
            val nameRouteGraphskoe = EntityDepartureStart.new { title = "Графское" }
            val nameRouteUglyanec = EntityDepartureStart.new { title = "Углянец" }
            val nameRouteVolya = EntityDepartureStart.new { title = "Воля" }

            val routeName310 = EntityRouteNumber.new { number = "310" }
            val routeName371 = EntityRouteNumber.new { number = "371" }
            val routeName302 = EntityRouteNumber.new { number = "302" }
            val routeName301 = EntityRouteNumber.new { number = "301" }
            val routeName312 = EntityRouteNumber.new { number = "312" }

            val route310 = EntityRoute.new {
                routeNumber = routeName310
                title = "Графское - Воронеж"
                description = ""
                departureTitleStart = nameRouteGraphskoe
                departureTitleEnd = nameRouteVoronezh
            }
            val route371 = EntityRoute.new {
                routeNumber = routeName371
                title = "Орлово - Воронеж"
                description = ""
                departureTitleStart = nameRouteOrlovo
                departureTitleEnd = nameRouteVoronezh
            }

            val route301 = EntityRoute.new {
                routeNumber = routeName301
                title = "Углянец - Воронеж"
                description = "Через Новую Усмань"
                departureTitleStart = nameRouteUglyanec
                departureTitleEnd = nameRouteVoronezh
            }

            val route302 = EntityRoute.new {
                routeNumber = routeName302
                title = "Углянец - Воронеж"
                description = "Через Бабяково"
                departureTitleStart = nameRouteUglyanec
                departureTitleEnd = nameRouteVoronezh
            }

            val route312 = EntityRoute.new {
                routeNumber = routeName312
                title = "Воля - Новая Усмань"
                description = ""
                departureTitleStart = nameRouteVolya
                departureTitleEnd = nameRouteNovayaUsman
            }

            timeList_310_graphskoe.forEach {
                val descriptionNew = if (it == "6:05") {
                    "Отправление от Парижской Коммуны"
                } else {
                    ""
                }
                EntityTimeStart.new {
                    route = route310
                    time = it
                    description = descriptionNew
                }
            }
            timeList_310_voronezh.forEach {
                val descriptionNew = if (it == "20:10") {
                    "Идёт до Парижской Коммуны"
                } else {
                    ""
                }
                EntityTimeEnd.new {
                    route = route310
                    time = it
                    description = descriptionNew
                }
            }
            timeList_371_orlovo.forEach {
                EntityTimeStart.new {
                    route = route371
                    time = it
                    description = ""
                }
            }

            timeList_371_voronezh.forEach {
                EntityTimeEnd.new {
                    route = route371
                    time = it
                    description = ""
                }
            }
            timeList_301_uglyanec.forEach {
                EntityTimeStart.new {
                    route = route301
                    time = it
                    description = ""
                }
            }
            timeList_301_voronezh.forEach {
                EntityTimeEnd.new {
                    route = route301
                    time = it
                    description = ""
                }
            }
            timeList_302_uglyanec.forEach {
                EntityTimeStart.new {
                    route = route302
                    time = it
                    description = ""
                }
            }

            timeList_302_voronezh.forEach {
                EntityTimeEnd.new {
                    route = route302
                    time = it
                    description = ""
                }
            }

            timeList_312_volya.forEach {
                EntityTimeStart.new {
                    route = route312
                    time = it
                    description = ""
                }
            }

            timeList_312_novaya_usman.forEach {
                EntityTimeEnd.new {
                    route = route312
                    time = it
                    description = ""
                }
            }
        }
    }
}