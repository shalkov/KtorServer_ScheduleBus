package ru.shalkoff.bus_schedule.base

data class GeneralResponse(
    override val info: InfoResponse
) : BaseResponse {
    companion object {

        private val EMPTY_MESSAGE = "Empty message"

        fun unauthorized(message: String?) = GeneralResponse(
            InfoResponse(
                State.UNAUTHORIZED,
                message ?: EMPTY_MESSAGE
            )
        )

        fun failed(message: String?) = GeneralResponse(
            InfoResponse(
                State.FAILED,
                message ?: EMPTY_MESSAGE
            )
        )

        fun notFound(message: String?) = GeneralResponse(
            InfoResponse(
                State.NOT_FOUND,
                message ?: EMPTY_MESSAGE
            )
        )

        fun success(message: String?) = GeneralResponse(
            InfoResponse(
                State.SUCCESS,
                message ?: EMPTY_MESSAGE
            )
        )

        fun serverError() = GeneralResponse(
            InfoResponse(
                State.FAILED,
                EMPTY_MESSAGE
            )
        )
    }
}