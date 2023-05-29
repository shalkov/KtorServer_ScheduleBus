package ru.shalkoff.bus_schedule.plugins

import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.Consts.ADMIN_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.INDEX_FTL
import ru.shalkoff.bus_schedule.Consts.LOGIN_FTL
import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Маршруты для админки
 */
fun Application.configureRoutingAdmin() {

    val userDao by inject<UsersDao>()
    val jwtHelper by inject<JWTHelper>()
    val passwordEncryptor by inject<PasswordEncryptor>()

    suspend fun checkUserAccessAdminPanel(call: ApplicationCall) {
        // получаем из кук, access токен
        val accessToken = call.request.cookies.rawCookies[Consts.ACCESS_TOKEN_PARAM]
        val userId = jwtHelper.verifyToken(accessToken ?: "")
        val user = userDao.getUserById(userId ?: -1)
        // проверяем, чтобы у пользователя была роль admin, иначе не даём досуп
        if (user == null || user.role != UserRole.ADMIN) {
            throw Exception("Нет прав доступа")
        }
    }

    routing {
        get(ADMIN_ENDPOINT) {
            try {
                checkUserAccessAdminPanel(call)
                call.respond(
                    FreeMarkerContent(
                        INDEX_FTL,
                        mapOf(
                            "users" to userDao.getAllUsers().sortedBy { it.id }
                        )
                    )
                )

            } catch (e: Exception) {
                call.respond(FreeMarkerContent(LOGIN_FTL, mapOf("error" to e.message)))
            }
        }

        route("admin/user") {
            get {
                try {
                    checkUserAccessAdminPanel(call)
                    val action = (call.request.queryParameters["action"] ?: "new")
                    when (action) {
                        "new" -> call.respond(
                            FreeMarkerContent(
                                "employee.ftl",
                                mapOf(
                                    "action" to action,
                                    "roles" to UserRole.values()
                                )
                            )
                        )

                        "edit" -> {
                            val id = call.request.queryParameters["id"]
                            if (id != null) {
                                call.respond(
                                    FreeMarkerContent(
                                        "employee.ftl",
                                        mapOf(
                                            "user" to userDao.getUserById(id.toInt()),
                                            "action" to action,
                                            "roles" to UserRole.values()
                                        )
                                    )
                                )
                            }
                        }
                    }

                } catch (e: Exception) {
                    call.respondRedirect(ADMIN_ENDPOINT)
                }
            }
            post {
                try {
                    checkUserAccessAdminPanel(call)
                    val postParameters: Parameters = call.receiveParameters()
                    val action = postParameters["action"] ?: "new"
                    when (action) {
                        //todo добавить проверку, чтобы нельзя было добавлять логин, который уже есть в БД
                        "new" -> userDao.addNewUser(
                            postParameters["login"] ?: "",
                            passwordEncryptor.encryptPassword(postParameters["password"] ?: ""),
                            postParameters["fullName"] ?: "",
                            postParameters["email"] ?: "",
                            UserRole.getByName(postParameters["role"] ?: "")
                        )

                        "edit" -> {
                            val id = postParameters["id"]
                            if (id != null)
                            //todo добавить проверку, чтобы нельзя было менять логин на ток, который уже есть в БД
                                userDao.editUser(
                                    id.toInt(),
                                    postParameters["login"] ?: "",
                                    passwordEncryptor.encryptPassword(postParameters["password"] ?: ""),
                                    postParameters["fullName"] ?: "",
                                    postParameters["email"] ?: "",
                                    UserRole.getByName(postParameters["role"] ?: "")
                                )
                        }
                    }
                    call.respondRedirect(ADMIN_ENDPOINT)
                } catch (e: Exception) {
                    call.respondRedirect(ADMIN_ENDPOINT)
                }
            }
        }

        route("admin/user/delete") {
            get {
                try {
                    checkUserAccessAdminPanel(call)
                    val id = call.request.queryParameters["id"]
                    if (id != null) {
                        userDao.deleteUser(id.toInt())
                        call.respondRedirect(ADMIN_ENDPOINT)
                    }
                } catch (e: Exception) {
                    call.respondRedirect(ADMIN_ENDPOINT)
                }
            }
        }
    }
}