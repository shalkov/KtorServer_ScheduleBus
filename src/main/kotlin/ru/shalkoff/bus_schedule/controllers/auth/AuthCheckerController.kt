package ru.shalkoff.bus_schedule.controllers.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts
import ru.shalkoff.bus_schedule.Consts.ROLE_TOKEN
import ru.shalkoff.bus_schedule.controllers.AdminController
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserModel

class AuthCheckerController : KoinComponent {

    private val userDao by inject<UsersDao>()
    private val adminController by inject<AdminController>()

    suspend fun getUserByToken(call: ApplicationCall): UserModel? {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim(Consts.USER_ID)?.asInt()
        val role = principal?.payload?.getClaim(ROLE_TOKEN)?.asString()
        val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())

        return if (userId == null) {
            // если в хедере не нашли токен, то смотрим в куки
            adminController.getUserByCookieAccess(call)
        } else {
            userDao.getUserById(userId)
        }
    }
}