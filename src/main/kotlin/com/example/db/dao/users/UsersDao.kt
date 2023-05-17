package com.example.db.dao.users

import com.example.db.models.User
import com.example.db.models.UserRole

/**
 * Интерфейс с методами, для работы с таблицей Users
 */
interface UsersDao {

    suspend fun allUsers(): List<User>
    suspend fun getUserById(id: Int): User?
    suspend fun addNewUser(
        login: String,
        password: String,
        fullName: String,
        email: String,
        roles: List<UserRole>
    ): User?
    suspend fun editUser(
        id: Int,
        login: String,
        password: String,
        fullName: String,
        email: String,
        roles: List<UserRole>
    ): Boolean
    suspend fun deleteUser(id: Int): Boolean

    suspend fun getUserByLogin(login: String): User?
}