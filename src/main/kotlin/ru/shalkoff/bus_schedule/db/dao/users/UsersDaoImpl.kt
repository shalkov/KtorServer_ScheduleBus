package ru.shalkoff.bus_schedule.db.dao.users

import ru.shalkoff.bus_schedule.db.DbFactory.dbQuery
import ru.shalkoff.bus_schedule.db.models.UserModel
import ru.shalkoff.bus_schedule.db.models.UserRole
import ru.shalkoff.bus_schedule.db.tables.UsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor

class UsersDaoImpl : UsersDao, KoinComponent {

    private val passwordEncryptor by inject<PasswordEncryptor>()

    override suspend fun getAllUsers(): List<UserModel> {
        return dbQuery {
            UsersTable.selectAll().map {
                resultRowToUser(it)
            }
        }
    }

    override suspend fun getUserById(id: Int): UserModel? {
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
    ): UserModel? {
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

    override suspend fun getUserByLogin(login: String): UserModel? {
        return dbQuery {
            UsersTable.select { UsersTable.login eq login }.map {
                resultRowToUser(it)
            }.singleOrNull()
        }
    }

    override suspend fun createDefaultSuperUser() {
        val superUserLogin = "admin"
        val adminUser = getUserByLogin(superUserLogin)
        if (adminUser != null) {
            deleteUser(adminUser.id)
        }
        addNewUser(
            superUserLogin,
            passwordEncryptor.encryptPassword("User_Admin_!!!"),
            "Admin Admin",
            "admin@admin.ru",
            UserRole.ADMIN
        )
    }

    private fun resultRowToUser(row: ResultRow) = UserModel(
        id = row[UsersTable.id],
        login = row[UsersTable.login],
        password = row[UsersTable.password],
        fullName = row[UsersTable.fullName],
        email = row[UsersTable.email],
        role = UserRole.getByName(row[UsersTable.role])
    )
}