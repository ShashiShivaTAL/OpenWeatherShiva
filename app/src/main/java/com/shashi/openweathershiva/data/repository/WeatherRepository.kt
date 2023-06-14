package com.shashi.openweathershiva.data.repository

import com.shashi.openweathershiva.data.network.Api
import com.shashi.openweathershiva.data.responses.WeatherLatLonResponse
import com.shashi.openweathershiva.data.responses.WeatherSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: Api
) : BaseRepository(api) {

    // Search weather by name
    suspend fun weatherSearch(cityName: String): Flow<WeatherSearchResponse> {
        return flow {
            val feedbackData = api.weatherSearch(cityName)
            emit(feedbackData)

        }.flowOn(Dispatchers.IO)
    }


    // Search weather by city name
    suspend fun searchWeatherLatLon(
        latitude: Double,
        longitude: Double
    ): Flow<WeatherLatLonResponse> {
        return flow {
            val feedbackData = api.searchWeatherLatLon(latitude, longitude)
            emit(feedbackData)

        }.flowOn(Dispatchers.IO)
    }

}