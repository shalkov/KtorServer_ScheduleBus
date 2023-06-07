package ru.shalkoff.bus_schedule.controllers.auth

import io.ktor.server.application.*
import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.auth.TokensModel
import java.text.SimpleDateFormat

abstract class BaseAuthController : KoinComponent {

    private val jwtHelper by inject<JWTHelper>()

    private val simpleDateFormat = SimpleDateFormat("'Date: 'yyyy-MM-dd' Time: 'HH:mm:ss")

    /**
     * Получить текущее время в нужном формате
     */
    fun getFormatCurrentTime(): String = simpleDateFormat.format((System.currentTimeMillis()))
    suspend fun addRefreshTokenToStore(
        userId: Int,
        refreshToken: String,
        tokensDao: TokensDao
    ) {
        //логика добавления рефреш-токена в БД
        val refreshExpirationTime = jwtHelper.getTokenExpiration(refreshToken)
        val refreshFormatExpirationTime = simpleDateFormat.format(refreshExpirationTime)
        tokensDao.addToken(
            userId = userId,
            refreshToken = refreshToken,
            expirationTime = refreshFormatExpirationTime
        )
    }

    fun saveTokensToCookie(
        tokens: TokensModel,
        call: ApplicationCall
    ) {
        call.response.cookies.append(
            Consts.ACCESS_TOKEN_PARAM,
            tokens.accessToken,
            maxAge = Consts.ACCESS_TOKEN_VALIDITY_ML / 1000, // миллисекунды в секунды переводим
            httpOnly = true
        )
        call.response.cookies.append(
            Consts.REFRESH_TOKEN_PARAM,
            tokens.refreshToken,
            maxAge = Consts.REFRESH_TOKEN_VALIDITY_ML / 1000, // миллисекунды в секунды переводим
            httpOnly = true
        )
    }
}