package ru.shalkoff.bus_schedule.base

import io.ktor.http.*

sealed class HttpResponse<T : BaseResponse> {
    abstract val body: T
    abstract val code: HttpStatusCode

    data class Ok<T : BaseResponse>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.OK
    }

    data class NotFound<T : BaseResponse>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.NotFound
    }

    data class BadRequest<T : BaseResponse>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.BadRequest
    }

    data class Unauthorized<T : BaseResponse>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.Unauthorized
    }

    data class ServerError<T : BaseResponse>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.InternalServerError
    }

    companion object {
        fun <T : BaseResponse> ok(response: T) = Ok(body = response)

        fun <T : BaseResponse> notFound(response: T) = NotFound(body = response)

        fun <T : BaseResponse> badRequest(response: T) = BadRequest(body = response)

        fun <T : BaseResponse> unauth(response: T) = Unauthorized(body = response)
        fun <T : BaseResponse> serverError(response: T) = ServerError(body = response)
    }
}

fun generateHttpResponse(response: BaseResponse): HttpResponse<BaseResponse> {
    return when (response.info.status) {
        State.SUCCESS -> HttpResponse.ok(response)
        State.NOT_FOUND -> HttpResponse.notFound(response)
        State.FAILED -> HttpResponse.badRequest(response)
        State.UNAUTHORIZED -> HttpResponse.unauth(response)
        State.SERVER_ERROR -> HttpResponse.serverError(GeneralResponse.serverError())
    }
}