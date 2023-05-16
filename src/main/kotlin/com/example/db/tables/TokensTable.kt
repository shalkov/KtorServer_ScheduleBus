package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object TokensTable : Table() {
    val userId = integer("userId")
    val refreshToken = varchar("refreshToken", 512)
    val expirationTime = varchar("expirationTime", 128)
}