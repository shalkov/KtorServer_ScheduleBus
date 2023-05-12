package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.auth.response.TokenResponse
import com.example.db.models.User
import java.util.*

class JWTHelperImpl : JWTHelper {

    private val secret = "bkFwb2xpdGE2OTk5"
    private val issuer = "bkFwb2xpdGE2OTk5"
    private val validityAccessInMs: Long = 1200000L // 20 минут
    private val refreshValidityInMs: Long = 3600000L * 24L * 30L // 30 дней
    private val algorithm = Algorithm.HMAC512(secret)

    override val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    override fun verifyToken(token: String): Int? {
        return verifier.verify(token).claims["userId"]?.asInt()
    }

    override fun getTokenExpiration(token: String): Date {
        return verifier.verify(token).expiresAt
    }

    /**
     * Создаёт пару токенов для выбранного пользователя
     */
    override fun createTokens(user: User) = TokenResponse(
        createAccessToken(user, createTokenExpirationDate(validityAccessInMs)),
        createRefreshToken(user, createTokenExpirationDate(refreshValidityInMs))
    )

    override fun verifyTokenType(token: String): String {
        return verifier.verify(token).claims["tokenType"]!!.asString()
    }

    private fun createAccessToken(user: User, expiration: Date) = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("userId", user.id)
        .withClaim("tokenType", "accessToken")
        .withExpiresAt(expiration)
        .sign(algorithm)

    private fun createRefreshToken(user: User, expiration: Date) = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("userId", user.id)
        .withClaim("tokenType", "refreshToken")
        .withExpiresAt(expiration)
        .sign(algorithm)

    /**
     * Сформировать дату "протухания" токена
     */
    private fun createTokenExpirationDate(validity: Long) = Date(System.currentTimeMillis() + validity)
}

interface JWTHelper {
    val verifier: JWTVerifier
    fun createTokens(user: User): TokenResponse
    fun verifyTokenType(token: String): String
    fun verifyToken(token: String): Int?
    fun getTokenExpiration(token: String): Date
}