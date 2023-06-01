package ru.shalkoff.bus_schedule.di

import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDao
import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDaoImpl
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.dao.users.UsersDaoImpl
import org.koin.dsl.module
import ru.shalkoff.bus_schedule.db.dao.schedule.ScheduleDao
import ru.shalkoff.bus_schedule.db.dao.schedule.ScheduleDaoImpl

object DaoModule {
    val koinBeans = module {
        single<TokensDao> { TokensDaoImpl() }
        single<UsersDao> { UsersDaoImpl() }
        single<ScheduleDao> { ScheduleDaoImpl() }
    }
}