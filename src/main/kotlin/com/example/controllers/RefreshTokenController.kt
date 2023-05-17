package com.example.controllers

import com.example.auth.JWTHelper
import com.example.auth.request.RefreshTokenRequest
import com.example.auth.response.TokenResponse
import com.example.base.BaseResponse
import com.example.base.GeneralResponse
import com.example.base.State
import com.example.db.dao.tokens.TokensDao
import com.example.db.dao.users.UsersDao
import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
                State.SUCCESS,
                "Токены обновлены успешно",
                tokens.accessToken,
                tokens.refreshToken
            )

        } catch (e: Exception) {
            GeneralResponse.failed(e.message)
        }
    }
}