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
        //todo подумать над более лучшим решением.
        // тут ошибка, поправить
        call.respondRedirect(Consts.ADMIN_ENDPOINT)
        //renderIndex(call, e.message)
    }

    suspend fun renderIndex(
        call: ApplicationCall,
        message: String? = null
    ) {
        try {
            checkUserAccessAdminPanel(call)
            call.respond(
                FreeMarkerContent(
                    Consts.INDEX_FTL,
                    mapOf("error_alert" to message)
                )
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Unauthorized,
                FreeMarkerContent(Consts.LOGIN_FTL, mapOf("error" to e.message))
            )
        }
    }
}