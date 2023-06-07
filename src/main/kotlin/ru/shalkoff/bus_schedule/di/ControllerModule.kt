package ru.shalkoff.bus_schedule.di

import ru.shalkoff.bus_schedule.controllers.auth.SignInController
import ru.shalkoff.bus_schedule.controllers.auth.RefreshTokenController
import ru.shalkoff.bus_schedule.controllers.auth.SignUpController
import org.koin.dsl.module
import ru.shalkoff.bus_schedule.controllers.AdminController
import ru.shalkoff.bus_schedule.controllers.ScheduleController
import ru.shalkoff.bus_schedule.controllers.auth.AuthCheckerController

object ControllerModule {
    val koinBeans = module {
        single<SignInController> { SignInController() }
        single<SignUpController> { SignUpController() }
        single<RefreshTokenController> { RefreshTokenController() }
        single<ScheduleController> { ScheduleController() }
        single<AdminController> { AdminController() }
        single<AuthCheckerController> { AuthCheckerController() }
    }
}