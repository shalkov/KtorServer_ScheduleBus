package com.example.di

import com.example.controllers.SignInController
import com.example.controllers.RefreshTokenController
import com.example.controllers.SignUpController
import org.koin.dsl.module

object ControllerModule {
    val koinBeans = module {
        single<SignInController> { SignInController() }
        single<SignUpController> { SignUpController() }
        single<RefreshTokenController> { RefreshTokenController() }
    }
}