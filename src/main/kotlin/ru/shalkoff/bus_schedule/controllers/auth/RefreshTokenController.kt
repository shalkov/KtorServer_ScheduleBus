package ru.shalkoff.bus_schedule.controllers.auth

import ru.shalkoff.bus_schedule.auth.JWTHelper
import ru.shalkoff.bus_schedule.network.request.RefreshTokenRequest
import ru.shalkoff.bus_schedule.network.response.TokenResponse
import ru.shalkoff.bus_schedule.base.BaseResponse
import ru.shalkoff.bus_schedule.base.GeneralResponse
import ru.shalkoff.bus_schedule.base.State
import ru.shalkoff.bus_schedule.db.dao.tokens.TokensDao
import ru.shalkoff.bus_schedule.db.dao.users.UsersDao
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.shalkoff.bus_schedule.base.InfoResponse

/**
 * Контроллер с логикой рефреш-токена
 */
class RefreshTokenController : BaseAuthController(), KoinComponent {

    private val userDao by inject<UsersDao>()
    private val tokensDao by inject<TokensDao>()
    private val jwtHelper by inject<JWTHelper>()

    suspend fun refresh(refreshTokenRequest: RefreshTokenRequest): BaseResponse {
        return try {
            val isRefreshToken = jwtHelper.isRefreshToken(refreshTokenRequest.refreshToken)
            // 1. Проверяем, что рефреш-токен валидный (тут же есть проверка и на срок его жизни)
            // Если всё хорошо, то получим id пользователя
            val userId = jwtHelper.verifyToken(refreshTokenRequest.refreshToken)
            if (!isRefreshToken || userId == null) {
                throw BadRequestException("Ошибка, при выполнении запроса /refresh. Рефреш-токен не валидный (или истёк его срок действия)")
            }
            // 2. Проверяем, есть ли в БД этот рефреш-токен
            val isRefreshTokenExists = tokensDao.exists(userId, refreshTokenRequest.refreshToken)
            // 3. В БД находим пользователя по его id, который пришёл в рефреш-токене
            val user = userDao.getUserById(userId)
            if (!isRefreshTokenExists || user == null) {
                throw BadRequestException("Ошибка, рефреш-токена нет БД или такой пользователь не существует")
            }
            // 4. Удаляем отправленный в запросе рефреш-токен из БД.
            // Чтобы его нельзя было использовать повторно, даже если он будет валидным по версии jwtHelper
            tokensDao.delete(refreshTokenRequest.refreshToken)
            // 5. Заодно, чтобы не засорять БД, удалим у пользователя все рефреш-токены, у которых истёк срок действия.
            // Такое может быть, так как для одного пользователя, может быть сгенерировано множество пар токенов.
            // (в текущей реализации не предусмотрено ограничение, на одну активную сессию для пользователя)
            tokensDao.deleteAllExpiredByUserId(user.id, getFormatCurrentTime())
            // 6. Генерируем новую пару токенов для пользователя
            val tokens = jwtHelper.createTokens(user)
            // 7. Сохраняем рефреш-токен в БД
            addRefreshTokenToStore(user.id, tokens.refreshToken, tokensDao)

            TokenResponse(
                tokens.accessToken,
                tokens.refreshToken,
                InfoResponse(
                    State.SUCCESS,
                    "Токены обновлены успешно",
                )
            )

        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }
}