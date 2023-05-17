package com.example.db.models

data class User(
    val id: Int,
    val login: String,
    val password: String,
    val fullName: String,
    val email: String,
    val roles: List<UserRole>
)

enum class UserRole(val role: String) {
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER");

    companion object {

        private val SEPARATOR: String = ","

        fun toEnumList(rolesStr: String): List<UserRole> {
            val rolesFromBd = rolesStr.split(SEPARATOR)
            val allRoles = UserRole.values() // все роли
            val resultRoles = mutableListOf<UserRole>()

            allRoles.forEach {
                if (rolesFromBd.contains(it.role)) {
                    resultRoles.add(it)
                }
            }
            return resultRoles
        }

        fun toString(roles: List<UserRole>): String {
            return roles.joinToString(SEPARATOR) { it.role }
        }
    }
}