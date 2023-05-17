package com.example.di

import com.example.db.dao.tokens.TokensDao
import com.example.db.dao.tokens.TokensDaoImpl
import com.example.db.dao.users.UsersDao
import com.example.db.dao.users.UsersDaoImpl
import org.koin.dsl.module

object DaoModule {
    val koinBeans = module {
        single<TokensDao> { TokensDaoImpl() }
        single<UsersDao> { UsersDaoImpl() }
    }
}