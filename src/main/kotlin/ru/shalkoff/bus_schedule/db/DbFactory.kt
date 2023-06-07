package ru.shalkoff.bus_schedule.db

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shalkoff.bus_schedule.db.tables.*
import java.util.*

object DbFactory {

    fun init() {
        val pool = hikari()
        val db = Database.connect(pool)

        val authTables = arrayOf(
            UsersTable,
            TokensTable,
        )
        val scheduleTables = arrayOf(
            Routes,
            RouteNumbers,
            TimesStart,
            TimesEnd,
            DeparturesStart,
            DeparturesEnd
        )
        val tables = authTables + scheduleTables
        transaction(db) {
            // удаляем таблици после каждого запуска сервера.
            SchemaUtils.drop(*tables)
            // таблицы заполняются в Application.
            SchemaUtils.createMissingTablesAndColumns(*tables)

        }
    }

    private fun getDatabaseProperties(): Properties {
        ConfigFactory.defaultApplication()
        val config = ConfigFactory.load().getConfig("db_postgres")
        val properties = Properties()
        config.entrySet().forEach { e -> properties.setProperty(e.key, config.getString(e.key)) }
        return properties
    }

    private fun hikari(): HikariDataSource {
        val hikariConfig = HikariConfig(getDatabaseProperties())
        return HikariDataSource(hikariConfig)
    }

    // Используем корутины для работы с БД, вместо блокирующих транзакций
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}