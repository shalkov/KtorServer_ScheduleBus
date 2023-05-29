package ru.shalkoff.bus_schedule.db.dao.tokens

import ru.shalkoff.bus_schedule.db.DbFactory
import ru.shalkoff.bus_schedule.db.models.Token
import ru.shalkoff.bus_schedule.db.tables.TokensTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less

class TokensDaoImpl : TokensDao {

    override suspend fun addToken(userId: Int, refreshToken: String, expirationTime: String): Token? {
        return DbFactory.dbQuery {
            val insertStatement = TokensTable.insert {
                it[TokensTable.userId] = userId
                it[TokensTable.refreshToken] = refreshToken
                it[TokensTable.expirationTime] = expirationTime
            }
            insertStatement.resultedValues?.singleOrNull()?.let {
                resultRowToToken(it)
            }
        }
    }

    override suspend fun updateToken(userId: Int, refreshToken: String, expirationTime: String): Boolean {
        return DbFactory.dbQuery {
            val updateStatementCount = TokensTable.update({
                TokensTable.userId eq userId
            }) {
                it[TokensTable.refreshToken] = refreshToken
                it[TokensTable.expirationTime] = expirationTime
            }
            updateStatementCount > 0
        }
    }

    override suspend fun getAllById(userId: Int): List<Token> {
        return DbFactory.dbQuery {
            TokensTable.select { (TokensTable.userId eq userId) }.map {
                resultRowToToken(it)
            }
        }
    }

    override suspend fun exists(userId: Int, token: String): Boolean {
        return DbFactory.dbQuery {
            TokensTable.select {
                ((TokensTable.refreshToken eq token) and (TokensTable.userId eq userId))
            }.map {
                resultRowToToken(it)
            }.firstOrNull() != null
        }
    }

    override suspend fun deleteAllExpiredByUserId(userId: Int, currentTime: String): Boolean {
        return DbFactory.dbQuery {
            val numberOfDeletedItems = TokensTable.deleteWhere {
                ((TokensTable.userId eq userId) and (expirationTime less currentTime))
            }
            numberOfDeletedItems > 0
        }
    }

    override suspend fun delete(refreshToken: String) {
        return DbFactory.dbQuery {
            val numberOfDeletedItems = TokensTable.deleteWhere {
                (TokensTable.refreshToken eq refreshToken)
            }
            numberOfDeletedItems > 0
        }
    }

    private fun resultRowToToken(row: ResultRow) = Token(
        userId = row[TokensTable.userId],
        refreshToken = row[TokensTable.refreshToken],
        expirationTime = row[TokensTable.expirationTime],
    )
}