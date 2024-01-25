package ru.shalkoff.bus_schedule

object Consts {

    // TODO Дефолтный путь, где будет находится сервер
    // Если нужно открытие в корне, изменить на пустую строку: ""
    private const val PROJECT_DEFAULT_PATH = "/projects/bus"

    const val EMPTY_STRING = ""

    // контстанты для JWT токена
    const val ACCESS_TOKEN_PARAM = "accessToken"
    const val REFRESH_TOKEN_PARAM = "refreshToken"
    const val USER_ID = "userId"
    const val TOKEN_TYPE = "tokenType"
    const val ROLE_TOKEN = "role"
    const val ACCESS_TOKEN_VALIDITY_ML = 1200000L // 20 минут
    const val REFRESH_TOKEN_VALIDITY_ML = 3600000L * 24L * 30L // 30 дней

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


    const val INDEX_ENDPOINT = PROJECT_DEFAULT_PATH
    const val SWAGGER_ENDPOINT = "$PROJECT_DEFAULT_PATH/swagger"

    // Кностанты связанные с админкой
    const val ADMIN_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin"
    const val ADMIN_USER_ALL_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/user/all"
    const val ADMIN_USER_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/user"
    const val ADMIN_USER_DELETE_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/user/delete"
    const val ADMIN_USER_URL = "$PROJECT_DEFAULT_PATH/admin/user"
    const val ADMIN_USER_DELETE_URL = "$PROJECT_DEFAULT_PATH/admin/user/delete"
    const val ADMIN_USER_ALL_URL = "$PROJECT_DEFAULT_PATH/admin/user/all"
    const val ADMIN_SCHEDULE_URL = "$PROJECT_DEFAULT_PATH/admin/schedule"
    const val ADMIN_SCHEDULE_DELETE_URL = "$PROJECT_DEFAULT_PATH/admin/schedule/delete"
    const val ADMIN_SCHEDULE_DEPARTURE_URL = "$PROJECT_DEFAULT_PATH/admin/schedule/departure"
    const val ADMIN_SCHEDULE_DEPARTURE_NEW_URL = "$PROJECT_DEFAULT_PATH/admin/schedule/departure/new"
    const val ADMIN_SCHEDULE_DEPARTURE_TIME_URL = "$PROJECT_DEFAULT_PATH/admin/schedule/departure/time"
    const val ADMIN_SCHEDULE_DEPARTURE_TIME_DELETE_URL = "$PROJECT_DEFAULT_PATH/admin/schedule/departure/time/delete"


    const val ADMIN_SCHEDULE_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/schedule"
    const val ADMIN_SCHEDULE_DEPARTURE_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/schedule/departure"
    const val ADMIN_SCHEDULE_DEPARTURE_NEW_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/schedule/departure/new"
    const val ADMIN_SCHEDULE_DEPARTURE_TIME_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/schedule/departure/time"
    const val ADMIN_SCHEDULE_DELETE_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/schedule/delete"
    const val ADMIN_SCHEDULE_DEPARTURE_TIME_DELETE_ENDPOINT = "$PROJECT_DEFAULT_PATH/admin/schedule/departure/time/delete"

    const val AUTH_ENDPOINT = "$PROJECT_DEFAULT_PATH/auth"
    const val USER_PROFILE_ENDPOINT = "$PROJECT_DEFAULT_PATH/user/profile"
    const val SCHEDULE_ALL_ENDPOINT = "$PROJECT_DEFAULT_PATH/schedule/all"
    const val SCHEDULE_ROUTE_NUMBER_ENDPOINT = "$PROJECT_DEFAULT_PATH/schedule/{route_number}"
    const val REGISTER_ENDPOINT = "$PROJECT_DEFAULT_PATH/register"
    const val REFRESH_ENDPOINT = "$PROJECT_DEFAULT_PATH/refresh"

    const val STATIC_FILES_PATH = "$PROJECT_DEFAULT_PATH/static"
}