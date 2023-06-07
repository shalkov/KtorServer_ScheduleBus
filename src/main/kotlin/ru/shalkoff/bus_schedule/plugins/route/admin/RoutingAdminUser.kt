package ru.shalkoff.bus_schedule.plugins.route.admin

import ru.shalkoff.bus_schedule.Consts.ADMIN_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.USERS_FTL
import ru.shalkoff.bus_schedule.auth.PasswordEncryptor
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts.USER_FTL
import ru.shalkoff.bus_schedule.auth.withRoles
import ru.shalkoff.bus_schedule.controllers.AdminController

/**
 * Маршруты для админки
 */
fun Application.configureRoutingAdminUser() {

    val adminController by inject<AdminController>()
    val userDao by inject<UsersDao>()
    val passwordEncryptor by inject<PasswordEncryptor>()

    routing {
        route("admin/user/all") {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    call.respond(
                        FreeMarkerContent(
                            USERS_FTL,
                            mapOf(
                                "users" to userDao.getAllUsers().sortedBy { it.id },
                                "error_alert" to adminController.alertError
                            )
                        )
                    )
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }

        route("admin/user") {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val action = (call.request.queryParameters["action"] ?: "new")
                    when (action) {
                        "new" -> call.respond(
                            FreeMarkerContent(
                                USER_FTL,
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
                                        USER_FTL,
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
                    adminController.handlerError(call, e)
                }
            }
            post {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val postParameters: Parameters = call.receiveParameters()
                    val action = postParameters["action"] ?: "new"
                    when (action) {
                        //todo добавить проверку, чтобы нельзя было добавлять логин, который уже есть в БД
                        "new" -> {
                            val login = postParameters["login"] ?: ""

                            val user = userDao.getUserByLogin(login)
                            if (user != null) {
                                throw BadRequestException("Пользователь с таким логином уже существует")
                            }
                            userDao.addNewUser(
                                login,
                                passwordEncryptor.encryptPassword(postParameters["password"] ?: ""),
                                postParameters["fullName"] ?: "",
                                postParameters["email"] ?: "",
                                UserRole.getByName(postParameters["role"] ?: "")
                            )
                        }

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
                    adminController.handlerError(call, e)
                }
            }
        }

        route("admin/user/delete") {
            get {
                try {
                    adminController.checkUserAccessAdminPanel(call)
                    val id = call.request.queryParameters["id"]
                    if (id != null) {
                        userDao.deleteUser(id.toInt())
                        call.respondRedirect(ADMIN_ENDPOINT)
                    }
                } catch (e: Exception) {
                    adminController.handlerError(call, e)
                }
            }
        }
    }
}