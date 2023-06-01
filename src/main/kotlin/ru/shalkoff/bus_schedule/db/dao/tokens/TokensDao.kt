package ru.shalkoff.bus_schedule.db.dao.tokens

import ru.shalkoff.bus_schedule.db.models.TokenModel

interface TokensDao {

    suspend fun addToken(
        userId: Int,
        refreshToken: String,
        expirationTime: String
    ): TokenModel?

    suspend fun updateToken(
        userId: Int,
        refreshToken: String,
        expirationTime: String
    ): Boolean

    suspend fun getAllById(userId: Int): List<TokenModel>

    suspend fun exists(userId: Int, token: String): Boolean
    suspend fun deleteAllExpiredByUserId(userId: Int, currentTime: String): Boolean
    suspend fun delete(refreshToken: String)
}