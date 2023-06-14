package com.shashi.openweathershiva.utils

import com.shashi.openweathershiva.BuildConfig

class Constants {
    companion object{
        const val API_KEY = BuildConfig.API_KEY
        const val SEARCH_WEATHER_TIME_DELAY = 1000L // milli seconds
        const val READ_TIMEOUT = 15 // seconds
        const val WRITE_TIMEOUT = 10 // seconds
        const val CONN_TIMEOUT = 10 // seconds
        const val LOCATION_PERMISSION_REQUEST_CODE = 50 // seconds
        const val REQUEST_LOCATION_PERMISSION = 1 // seconds
    }
}