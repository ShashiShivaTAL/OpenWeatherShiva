package com.shashi.openweathershiva.data.repository

import com.shashi.openweathershiva.data.network.Api
import com.shashi.openweathershiva.data.network.SafeApiCall

abstract class BaseRepository(private val api: Api) : SafeApiCall {

    suspend fun logout() = safeApiCall {
        api.logout()
    }
}