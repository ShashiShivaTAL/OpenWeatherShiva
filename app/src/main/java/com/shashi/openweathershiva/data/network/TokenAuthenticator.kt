package com.shashi.openweathershiva.data.network

import android.content.Context
import com.shashi.openweathershiva.data.UserPreferences
import com.shashi.openweathershiva.data.repository.BaseRepository
import com.shashi.openweathershiva.data.responses.TokenResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    context: Context,
    private val tokenApi: Api
) : Authenticator, BaseRepository(tokenApi) {

    private val appContext = context.applicationContext
    private val userPreferences = UserPreferences(appContext)

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            when (val tokenResponse = getUpdatedToken()) {
                is Resource.Success -> {
                    userPreferences.saveAccessTokens(
                        tokenResponse.data.access_token!!,
                        //tokenResponse.data.refresh_token!!
                    )
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.data.access_token}")
                        .build()
                }
                else -> null
            }
        }
    }

    private suspend fun getUpdatedToken(): Resource<TokenResponse> {
        // val refreshToken = userPreferences.refreshToken.first()
        // return safeApiCall { tokenApi.refreshAccessToken(refreshToken) }
        return safeApiCall { tokenApi.refreshAccessToken(null) }
    }

}