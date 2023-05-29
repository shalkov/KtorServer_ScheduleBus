package ru.shalkoff.bus_schedule.plugins

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.auth.JWTHelperImpl
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor
import ru.shalkoff.bus_schedule.auth.PasswordEncryptorImpl
import ru.shalkoff.bus_schedule.di.ControllerModule
import ru.shalkoff.bus_schedule.di.DaoModule


fun Application.configureKoin() {

    install(Koin) {
        slf4jLogger(level = org.koin.core.logger.Level.ERROR)
        modules(
            module {
                single<JWTHelper> { JWTHelperImpl() }
                single<PasswordEncryptor> { PasswordEncryptorImpl() }
            },
            DaoModule.koinBeans,
            ControllerModule.koinBeans
        )
    }
}