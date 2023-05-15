package com.example.db.dao.tokens

import com.example.db.models.Token

interface TokensDao {

    suspend fun addToken(
        userId: Int,
        refreshToken: String,
        expiresAt: Long
    ): Token?

    suspend fun updateToken(
        userId: Int,
        refreshToken: String,
        expiresAt: Long
    ): Boolean
}