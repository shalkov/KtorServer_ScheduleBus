package ru.shalkoff.bus_schedule

object Consts {

    const val EMPTY_STRING = ""

    // контстанты для JWT токена
    const val ACCESS_TOKEN_PARAM = "accessToken"
    const val REFRESH_TOKEN_PARAM = "refreshToken"
    const val USER_ID = "userId"
    const val TOKEN_TYPE = "tokenType"
    const val ROLE_TOKEN = "role"
    const val ACCESS_TOKEN_VALIDITY_ML = 1200000L // 20 минут
    const val REFRESH_TOKEN_VALIDITY_ML = 3600000L * 24L * 30L // 30 дней

    const val UNKNOWN_ERROR = "Неизвестная ошибка"

    const val FREEMARKER_BASE_PATH = "admin_panel_ftl"
    const val INDEX_FTL = "index.ftl"
    const val USERS_FTL = "users.ftl"
    const val USER_FTL = "user.ftl"
    const val LOGIN_FTL = "login.ftl"
    const val SCHEDULE_FTL = "schedule.ftl"
    const val SCHEDULE_NEW_EDIT_FTL = "schedule_new_edit.ftl"
    const val DEPARTURE_FTL = "departure.ftl"
    const val DEPARTURE_NEW_FTL = "departure_new.ftl"
    const val TIME_NEW_EDIT_FTL = "time_new_edit.ftl"

    const val INDEX_ENDPOINT = "/"
    const val SWAGGER_ENDPOINT = "/swagger"
    const val ADMIN_ENDPOINT = "/admin"
    const val ADMIN_SCHEDULE_ENDPOINT = "/admin/schedule"
    const val ADMIN_SCHEDULE_DEPARTURE_ENDPOINT = "/admin/schedule/departure"
    const val AUTH_ENDPOINT = "/auth"
    const val REGISTER_ENDPOINT = "/register"
    const val REFRESH_ENDPOINT = "/refresh"
}