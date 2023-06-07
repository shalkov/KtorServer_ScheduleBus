package ru.shalkoff.bus_schedule.db.dao.users

import ru.shalkoff.bus_schedule.db.models.UserModel
import ru.shalkoff.bus_schedule.db.models.UserRole

/**
 * Интерфейс с методами, для работы с таблицей Users
 */
interface UsersDao {

    suspend fun getAllUsers(): List<UserModel>
    suspend fun getUserById(id: Int): UserModel?
    suspend fun addNewUser(
        login: String,
        password: String,
        fullName: String,
        email: String,
        role: UserRole
    ): UserModel?
    suspend fun editUser(
        id: Int,
        login: String,
        password: String,
        fullName: String,
        email: String,
        role: UserRole
    ): Boolean
    suspend fun deleteUser(id: Int): Boolean

    suspend fun getUserByLogin(login: String): UserModel?

    suspend fun createDefaultSuperUser()
}