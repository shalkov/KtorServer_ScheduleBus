package ru.shalkoff.bus_schedule.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
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
        val byteMessage = e.message?.toByteArray() ?: byteArrayOf()
        val str = String(byteMessage, Charsets.ISO_8859_1)
        call.respondRedirect(Consts.ADMIN_ENDPOINT+"?errorText=${str}")
    }
}