package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 128)
    val password = varchar("password", 1024)
    val fullName = text("fullName")
    val email = text("email")
    val role = text("role")

    override val primaryKey = PrimaryKey(id)
}