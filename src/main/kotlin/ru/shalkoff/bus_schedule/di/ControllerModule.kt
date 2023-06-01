package ru.shalkoff.bus_schedule.di

import ru.shalkoff.bus_schedule.controllers.auth.SignInController
import ru.shalkoff.bus_schedule.controllers.auth.RefreshTokenController
import ru.shalkoff.bus_schedule.controllers.auth.SignUpController
import org.koin.dsl.module
import ru.shalkoff.bus_schedule.controllers.ScheduleController

object ControllerModule {
    val koinBeans = module {
        single<SignInController> { SignInController() }
        single<SignUpController> { SignUpController() }
        single<RefreshTokenController> { RefreshTokenController() }
        single<ScheduleController> { ScheduleController() }
    }
}