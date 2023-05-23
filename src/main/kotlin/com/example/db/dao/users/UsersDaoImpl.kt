package com.example.db.dao.users

import com.example.db.DbFactory.dbQuery
import com.example.db.models.User
import com.example.db.models.UserRole
import com.example.db.tables.UsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UsersDaoImpl : UsersDao {

    override suspend fun getAllUsers(): List<User> {
        return dbQuery {
            UsersTable.selectAll().map {
                resultRowToUser(it)
            }
        }
    }

    override suspend fun getUserById(id: Int): User? {
        return dbQuery {
            UsersTable
                .select { UsersTable.id eq id }
                .map {
                    resultRowToUser(it)
                }
                .singleOrNull()
        }
    }

    override suspend fun addNewUser(
        login: String,
        password: String,
        fullName: String,
        email: String,
        role: UserRole
    ): User? {
        return dbQuery {
            val insertStatement = UsersTable.insert {
                it[UsersTable.login] = login
                it[UsersTable.password] = password
                it[UsersTable.fullName] = fullName
                it[UsersTable.email] = email
                it[UsersTable.role] = role.roleStr
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
        fullName: String,
        email: String,
        role: UserRole
    ): Boolean {
        return dbQuery {
            val updateStatementCount = UsersTable.update({
                UsersTable.id eq id
            }) {
                it[UsersTable.login] = login
                it[UsersTable.password] = password
                it[UsersTable.fullName] = fullName
                it[UsersTable.email] = email
                it[UsersTable.role] = role.roleStr
            }
            updateStatementCount > 0
        }
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id } > 0
        }
    }

    override suspend fun getUserByLogin(login: String): User? {
        return dbQuery {
            UsersTable.select { UsersTable.login eq login }.map {
                resultRowToUser(it)
            }.singleOrNull()
        }
    }

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UsersTable.id],
        login = row[UsersTable.login],
        password = row[UsersTable.password],
        fullName = row[UsersTable.fullName],
        email = row[UsersTable.email],
        role = UserRole.getByName(row[UsersTable.role])
    )
}