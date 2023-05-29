package ru.shalkoff.bus_schedule

object Consts {

    const val USER_ID = "userId"

    const val ACCESS_TOKEN_PARAM = "accessToken"
    const val REFRESH_TOKEN_PARAM = "refreshToken"

    const val ACCESS_TOKEN_VALIDITY_ML = 1200000L // 20 минут
    const val REFRESH_TOKEN_VALIDITY_ML = 3600000L * 24L * 30L // 30 дней

    const val FREEMARKER_BASE_PATH = "admin_panel_ftl"
    const val INDEX_FTL = "index.ftl"
    const val LOGIN_FTL = "login.ftl"

    const val INDEX_ENDPOINT = "/"
    const val ADMIN_ENDPOINT = "/admin"
    const val AUTH_ENDPOINT = "/auth"
    const val REGISTER_ENDPOINT = "/register"
    const val REFRESH_ENDPOINT = "/refresh"
}