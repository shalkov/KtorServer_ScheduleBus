package com.example.db.dao.tokens

import com.example.db.DbFactory
import com.example.db.models.Token
import com.example.db.tables.TokensTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

class TokensDaoImpl : TokensDao {

    override suspend fun addToken(userId: Int, refreshToken: String, expiresAt: Long): Token? {
        return DbFactory.dbQuery {
            val insertStatement = TokensTable.insert {
                it[TokensTable.userId] = userId
                it[TokensTable.refreshToken] = refreshToken
                it[TokensTable.expiresAt] = expiresAt
            }
            insertStatement.resultedValues?.singleOrNull()?.let {
                resultRowToToken(it)
            }
        }
    }

    override suspend fun updateToken(userId: Int, refreshToken: String, expiresAt: Long): Boolean {
        return DbFactory.dbQuery {
            val updateStatementCount = TokensTable.update({
                TokensTable.userId eq userId
            }) {
                it[TokensTable.refreshToken] = refreshToken
                it[TokensTable.expiresAt] = expiresAt
            }
            updateStatementCount > 0
        }
    }

    private fun resultRowToToken(row: ResultRow) = Token(
        userId = row[TokensTable.userId],
        refreshToken = row[TokensTable.refreshToken],
        expiresAt = row[TokensTable.expiresAt],
    )
}