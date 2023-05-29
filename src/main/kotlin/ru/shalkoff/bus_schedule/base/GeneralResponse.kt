package ru.shalkoff.bus_schedule.base

data class GeneralResponse(
    override val status: State,
    override val message: String
) : BaseResponse {
    companion object {

        private val EMPTY_MESSAGE = "Empty message"

        fun unauthorized(message: String?) = GeneralResponse(
            State.UNAUTHORIZED,
            message  ?: EMPTY_MESSAGE
        )

        fun failed(message: String?) = GeneralResponse(
            State.FAILED,
            message ?: EMPTY_MESSAGE
        )

        fun notFound(message: String?) = GeneralResponse(
            State.NOT_FOUND,
            message ?: EMPTY_MESSAGE
        )

        fun success(message: String?) = GeneralResponse(
            State.SUCCESS,
            message ?: EMPTY_MESSAGE
        )

        fun serverError() = GeneralResponse(
            State.FAILED,
            EMPTY_MESSAGE
        )
    }
}