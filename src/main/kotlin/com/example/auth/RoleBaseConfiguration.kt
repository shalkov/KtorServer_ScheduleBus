package com.example.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Класс инкапсулирует логику, по разграничению ролей пользователей
 */
class RoleBaseConfiguration {
    val requiredRoles = mutableSetOf<String>()
    fun roles(roles: Set<String>) {
        requiredRoles.addAll(roles)
    }
}
fun Route.withRoles(vararg roles: String, build: Route.() -> Unit) {
    val route = createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
            return RouteSelectorEvaluation.Transparent
        }

    })
    route.install(RoleAuthorizationPlugin) {
        roles(roles.toSet())
    }

    route.build()
}

val RoleAuthorizationPlugin = createRouteScopedPlugin("RoleAuthorizationPlugin", ::RoleBaseConfiguration) {
    on(AuthenticationChecked) { call ->
        val principal = call.principal<JWTPrincipal>() ?: return@on
        val roles = principal.payload.getClaim("role").asString().toSet()

        if (pluginConfig.requiredRoles.isNotEmpty() && roles.intersect(pluginConfig.requiredRoles).isEmpty()) {
            call.respondText("У вас нет доступа для просмотра этого ресурса", status = HttpStatusCode.Unauthorized)
        }
    }
}