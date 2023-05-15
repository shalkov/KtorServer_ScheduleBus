package com.example.db

import com.example.db.tables.TokensTable
import com.example.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DbFactory {

    fun init() {
        val pool = hikari()
        val db = Database.connect(pool)

        val tables = arrayOf(UsersTable, TokensTable)
        transaction(db) {
            migrationDb()
            //SchemaUtils.drop(*tables)
            SchemaUtils.createMissingTablesAndColumns(*tables)
        }
    }

    private fun migrationDb() {
        //todo добавить логику миграции БД
//            val flyway = Flyway.configure().dataSource(
//                "jdbc:postgresql://localhost:5432/test-db",
//                "test-user",
//                "test-password"
//            ).load()
//            flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        val hikariConfig = HikariConfig("db.properties")
        return HikariDataSource(hikariConfig)
    }

    // Используем корутины для работы с БД, вместо блокирующих транзакций
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}