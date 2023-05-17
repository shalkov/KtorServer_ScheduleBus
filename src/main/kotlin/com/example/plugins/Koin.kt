package com.example.plugins

import com.example.auth.JWTHelper
import com.example.auth.JWTHelperImpl
import com.example.auth.PasswordEncryptor
import com.example.auth.PasswordEncryptorImpl
import com.example.di.ControllerModule
import com.example.di.DaoModule
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


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