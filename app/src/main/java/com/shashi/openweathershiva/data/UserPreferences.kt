package com.shashi.openweathershiva.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "five_element_homes_data_store")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val appContext = context.applicationContext

    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    val lastWeatherLocationValue: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[LAST_WEATHER_LOCATION_VALUE]
        }

    val lastWeatherSearchValue: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[LAST_WEATHER_SEARCH_VALUE]
        }

    suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveAccessTokens(accessToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveLastWeatherLocationValue(weather: String) {
        appContext.dataStore.edit { preferences ->
            preferences[LAST_WEATHER_LOCATION_VALUE] = weather
        }
    }

    suspend fun saveLastWeatherSearchValue(weather: String) {
        appContext.dataStore.edit { preferences ->
            preferences[LAST_WEATHER_SEARCH_VALUE] = weather
        }
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val LAST_WEATHER_LOCATION_VALUE = stringPreferencesKey("last_weather_location_value")
        private val LAST_WEATHER_SEARCH_VALUE = stringPreferencesKey("last_weather_search_value")
    }


}