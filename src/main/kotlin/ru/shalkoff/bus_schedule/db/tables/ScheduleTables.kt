package ru.shalkoff.bus_schedule.db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Список всех наименований маршрутов
 */
object RouteNumbers : IntIdTable() {
    val number = text("route_number").uniqueIndex() // 310
}

/**
 * Список всех маршрутов
 */
object Routes : IntIdTable() {

    val number = reference("number", RouteNumbers.number)
    val title = text("route_title") // Графское - Воронеж (310)
    val description = text("route_description") // Длинное описание маршрута
    val departureTitleStart = reference("departure_start", DeparturesStart.title)
    val departureTitleEnd = reference("departure_end", DeparturesEnd.title)
}

/**
 * Список со всеми временами отправлени из начальной точки
 */
object TimesStart : IntIdTable() {

    val routeId = reference("route_id", Routes)
    val time = text("time")
    val description = text("description")
}

/**
 * Список со всеми временами отправлени из конечной точки
 */
object TimesEnd : IntIdTable() {

    val routeId = reference("route_id", Routes)
    val time = text("time")
    val description = text("description")
}

object DeparturesStart : IntIdTable() {

    val title = text("departure_title_start").uniqueIndex()
}

object DeparturesEnd : IntIdTable() {

    val title = text("departure_title_end").uniqueIndex()
}


// ----------------------- Entity --------------------- //

class EntityRouteNumber(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<EntityRouteNumber>(RouteNumbers)

    var number by RouteNumbers.number
}

class EntityRoute(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntityRoute>(Routes)

    var routeNumber by EntityRouteNumber referencedOn Routes.number
    var title by Routes.title
    var description by Routes.description
    var departureTitleStart by EntityDepartureStart referencedOn Routes.departureTitleStart
    var departureTitleEnd by EntityDepartureEnd referencedOn Routes.departureTitleEnd
}

class EntityTimeStart(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntityTimeStart>(TimesStart)

    var route by EntityRoute referencedOn TimesStart.routeId
    var time by TimesStart.time
    var description by TimesStart.description
}

class EntityTimeEnd(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntityTimeEnd>(TimesEnd)

    var route by EntityRoute referencedOn TimesEnd.routeId
    var time by TimesEnd.time
    var description by TimesEnd.description
}

class EntityDepartureStart(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<EntityDepartureStart>(DeparturesStart)

    var title by DeparturesStart.title
}

class EntityDepartureEnd(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<EntityDepartureEnd>(DeparturesEnd)

    var title by DeparturesEnd.title
}