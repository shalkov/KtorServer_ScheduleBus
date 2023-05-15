package com.example.db.dao.users

import com.example.db.models.User

/**
 * Интерфейс с методами, для работы с таблицей Users
 */
interface UsersDao {

    suspend fun allUsers(): List<User>
    suspend fun user(id: Int): User?
    suspend fun addNewUser(
        login: String,
        password: String,
        fullName: String,
        email: String
    ): User?
    suspend fun editUser(
        id: Int,
        login: String,
        password: String,
        fullName: String,
        email: String
    ): Boolean
    suspend fun deleteUser(id: Int): Boolean

    suspend fun getUserByLogin(login: String): User?
}