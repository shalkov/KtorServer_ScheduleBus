package ru.shalkoff.bus_schedule.plugins

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import ru.shalkoff.bus_schedule.Consts.FREEMARKER_BASE_PATH

fun Application.configureFreemarker() {

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, FREEMARKER_BASE_PATH)
    }

}