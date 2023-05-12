package com.example.db.models

import org.jetbrains.exposed.sql.*

data class User(
    val id: Int,
    val login: String,
    val password: String,
    val fullName: String,
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 128)
    val password = varchar("password", 1024)
    val fullName = text("fullName")

    override val primaryKey = PrimaryKey(id)
}