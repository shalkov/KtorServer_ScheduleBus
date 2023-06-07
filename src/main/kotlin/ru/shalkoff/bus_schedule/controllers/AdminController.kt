package ru.shalkoff.bus_schedule.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserModel
import ru.shalkoff.bus_schedule.db.models.UserRole

class AdminController : KoinComponent {

    private val userDao by inject<UsersDao>()
    private val jwtHelper by inject<JWTHelper>()

    var alertError: String = ""
        get() {
            val error = field
            field = ""
            return error
        }

    fun getAccessTokenFromCookie(call: ApplicationCall): String? {
        return call.request.cookies.rawCookies[Consts.ACCESS_TOKEN_PARAM]
    }

    suspend fun getUserByCookieAccess(call: ApplicationCall): UserModel? {
        // получаем из кук, access токен
        val accessToken = getAccessTokenFromCookie(call)
        val userId = jwtHelper.verifyToken(accessToken ?: "")
        return if (userId != null) {
            userDao.getUserById(userId)
        } else {
            null
        }
    }

    suspend fun checkUserAccessAdminPanel(call: ApplicationCall) {
        val user = getUserByCookieAccess(call)
        // проверяем, чтобы у пользователя была роль admin, иначе не даём досуп
        if (user == null || user.role != UserRole.ADMIN) {
            throw Exception("Нет прав доступа")
        }
    }

    suspend fun handlerError(call: ApplicationCall, e: Exception) {
        //todo написать логику проверки
        //alertError = e.message ?: Consts.UNKNOWN_ERROR
        call.respondRedirect(Consts.ADMIN_ENDPOINT)
    }
}