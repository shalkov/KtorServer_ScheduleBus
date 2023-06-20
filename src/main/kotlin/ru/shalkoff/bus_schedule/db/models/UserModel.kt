package ru.shalkoff.bus_schedule.db.models

data class UserModel(
    val id: Int,
    val login: String,
    val password: String,
    val fullName: String,
    val email: String,
    val role: UserRole
)

data class UserModelResponse(
    val id: Int,
    val login: String,
    val fullName: String,
    val email: String,
    val role: UserRole
)

enum class UserRole(val roleStr: String) {
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER");

    companion object {

        fun getByName(roleStr: String): UserRole {
            return UserRole.values().firstOrNull { it.roleStr == roleStr } ?: USER
        }

        fun UserModel.mapToResponse(): UserModelResponse {
            return UserModelResponse(
                this.id,
                this.login,
                this.fullName,
                this.email,
                this.role
            )
        }
    }
}