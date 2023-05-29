package ru.shalkoff.bus_schedule.di

import ru.shalkoff.bus_schedule.controllers.SignInController
import ru.shalkoff.bus_schedule.controllers.RefreshTokenController
import ru.shalkoff.bus_schedule.controllers.SignUpController
import org.koin.dsl.module

object ControllerModule {
    val koinBeans = module {
        single<SignInController> { SignInController() }
        single<SignUpController> { SignUpController() }
        single<RefreshTokenController> { RefreshTokenController() }
    }
}