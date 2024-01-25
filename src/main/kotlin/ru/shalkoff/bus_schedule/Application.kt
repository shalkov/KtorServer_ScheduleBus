package ru.shalkoff.bus_schedule

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.db.DbFactory
import ru.shalkoff.bus_schedule.db.dao.schedule.ScheduleDao
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.plugins.*
import ru.shalkoff.bus_schedule.plugins.route.admin.configureRoutingAdmin
import ru.shalkoff.bus_schedule.plugins.route.configureRouting
import ru.shalkoff.bus_schedule.plugins.route.admin.configureRoutingAdminSchedule
import ru.shalkoff.bus_schedule.plugins.route.admin.configureRoutingAdminUser
import ru.shalkoff.bus_schedule.plugins.route.configureRoutingProfile
import ru.shalkoff.bus_schedule.plugins.route.configureRoutingSchedule

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ) .start(wait = true)
}

fun Application.module() {
    val scheduleDao by inject<ScheduleDao>()
    val userDao by inject<UsersDao>()

    DbFactory.init()
    configureSerialization()
    configureKoin()
    configureSwaggerUI()
    configureSecurity()
    configureFreemarker()

    // api запросы
    routing {
        get(Consts.INDEX_ENDPOINT) {
            call.respondRedirect(Consts.SWAGGER_ENDPOINT)
        }
    }
    configureRouting()
    configureRoutingSchedule()
    configureRoutingProfile()

    //маршруты для админки
    configureRoutingAdmin()
    configureRoutingAdminUser()
    configureRoutingAdminSchedule()

    environment.monitor.subscribe(ApplicationStarted) { application ->
        application.environment.log.info("Сервер запущен!")
        runBlocking {
            // после старта сервера, заполняем таблицы данными
            scheduleDao.setupDefaultSchedule()
            userDao.createDefaultSuperUser()
        }
    }
}