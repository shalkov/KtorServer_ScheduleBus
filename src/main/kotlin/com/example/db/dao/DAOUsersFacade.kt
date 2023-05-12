package com.example.db.dao

import com.example.db.models.User

/**
 * Интерфейс с методами, для работы с таблицей Users
 */
interface DAOUsersFacade {

    suspend fun allUsers(): List<User>
    suspend fun user(id: Int): User?
    suspend fun addNewUser(
        login: String,
        password: String,
        fullName: String
    ): User?
    suspend fun editUser(
        id: Int,
        login: String,
        password: String,
        fullName: String
    ): Boolean
    suspend fun deleteUser(id: Int): Boolean
}