package ru.shalkoff.bus_schedule.db.dao.tokens

import ru.shalkoff.bus_schedule.db.models.Token

interface TokensDao {

    suspend fun addToken(
        userId: Int,
        refreshToken: String,
        expirationTime: String
    ): Token?

    suspend fun updateToken(
        userId: Int,
        refreshToken: String,
        expirationTime: String
    ): Boolean

    suspend fun getAllById(userId: Int): List<Token>

    suspend fun exists(userId: Int, token: String): Boolean
    suspend fun deleteAllExpiredByUserId(userId: Int, currentTime: String): Boolean
    suspend fun delete(refreshToken: String)
}