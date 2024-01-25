package ru.shalkoff.bus_schedule.plugins.route

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import org.koin.ktor.ext.inject
import ru.shalkoff.bus_schedule.Consts.AUTH_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.REFRESH_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.REGISTER_ENDPOINT
import ru.shalkoff.bus_schedule.Consts.STATIC_FILES_PATH
import ru.shalkoff.bus_schedule.Consts.USER_ID
import ru.shalkoff.bus_schedule.network.request.RefreshTokenRequest
import ru.shalkoff.bus_schedule.auth.withRoles
import ru.shalkoff.bus_schedule.base.generateHttpResponse
import ru.shalkoff.bus_schedule.controllers.auth.RefreshTokenController
import ru.shalkoff.bus_schedule.controllers.auth.SignInController
import ru.shalkoff.bus_schedule.controllers.auth.SignUpController
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import ru.shalkoff.bus_schedule.db.models.UserRole

fun Application.configureRouting() {

    val signInController by inject<SignInController>()
    val signUpController by inject<SignUpController>()
    val refreshTokenController by inject<RefreshTokenController>()
    val userDao by inject<UsersDao>()

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        post(AUTH_ENDPOINT) {
            val authResponse = signInController.signIn(call)
            val response = generateHttpResponse(authResponse)

            call.respond(response.code, response.body)
        }

        post(REGISTER_ENDPOINT) {
            val registerResponse = signUpController.signUp(call)
            val response = generateHttpResponse(registerResponse)

            call.respond(response.code, response.body)
        }

        post(REFRESH_ENDPOINT) {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val refreshTokenResponse = refreshTokenController.refresh(refreshTokenRequest)
            val response = generateHttpResponse(refreshTokenResponse)
            call.respond(response.code, response.body)
        }

        authenticate {
            // Авторизованная зона и роль админ
            withRoles(UserRole.ADMIN.roleStr) {
                get("/admin000") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim(USER_ID)?.asInt()
                    val roles = principal?.payload?.getClaim("role")?.asList(UserRole::class.java)?.toSet()
                    val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())

                    val user = userDao.getUserById(userId ?: 0)
                    call.respondText(
                        "Привет, ${user?.login}! Токен протухнет через: $expiresAt ms.\n" +
                                "$roles"
                    )

                }
            }

            withRoles {
                get("/anyone") {
                    call.respondText { "For anyone" }
                }
            }
        }

        // Static plugin. Try to access `/static/index.html`
        static(STATIC_FILES_PATH) {
            resources("static")
        }
    }
}
