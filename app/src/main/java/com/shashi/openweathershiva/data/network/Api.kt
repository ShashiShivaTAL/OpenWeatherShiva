package com.shashi.openweathershiva.data.network

import com.shashi.openweathershiva.data.responses.TokenResponse
import com.shashi.openweathershiva.data.responses.WeatherLatLonResponse
import com.shashi.openweathershiva.data.responses.WeatherSearchResponse
import com.shashi.openweathershiva.utils.Constants.Companion.API_KEY
import okhttp3.ResponseBody
import retrofit2.http.*

interface Api {

    @POST("logout")
    suspend fun logout(): ResponseBody

    @FormUrlEncoded
    @POST("auth/refresh-token")
    suspend fun refreshAccessToken(
        @Field("refresh_token") refreshToken: String?,
    ): TokenResponse


    @GET("data/2.5/weather")
    suspend fun weatherSearch(
        @Query("q")
        cityName: String,
        @Query("appid")
        appId: String = API_KEY
    ): WeatherSearchResponse

    @GET("data/2.5/weather")
    suspend fun searchWeatherLatLon(
        @Query("lat")
        latitude: Double,
        @Query("lon")
        longitude: Double,
        @Query("appid")
        appId: String = API_KEY
    ): WeatherLatLonResponse
}
