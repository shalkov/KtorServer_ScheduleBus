package com.example.db.dao

import com.example.db.DbFactory.dbQuery
import com.example.db.models.User
import com.example.db.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOUsersFacadeImpl : DAOUsersFacade {

    override suspend fun allUsers(): List<User> {
        return dbQuery {
            Users.selectAll().map {
                resultRowToUser(it)
            }
        }
    }

    override suspend fun user(id: Int): User? {
        return dbQuery {
            Users
                .select { Users.id eq id }
                .map {
                    resultRowToUser(it)
                }
                .singleOrNull()
        }
    }

    override suspend fun addNewUser(
        login: String,
        password: String,
        fullName: String
    ): User? {
        return dbQuery {
            val insertStatement = Users.insert {
                it[Users.login] = login
                it[Users.password] = password
                it[Users.fullName] = fullName
            }
            insertStatement.resultedValues?.singleOrNull()?.let {
                resultRowToUser(it)
            }
        }
    }

    override suspend fun editUser(
        id: Int,
        login: String,
        password: String,
        fullName: String
    ): Boolean {
        return dbQuery {
            val updateStatementCount = Users.update({
                Users.id eq id
            }) {
                it[Users.login] = login
                it[Users.password] = password
                it[Users.fullName] = password
            }
            updateStatementCount > 0
        }
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return dbQuery {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        login = row[Users.login],
        password = row[Users.password],
        fullName = row[Users.fullName]
    )
}