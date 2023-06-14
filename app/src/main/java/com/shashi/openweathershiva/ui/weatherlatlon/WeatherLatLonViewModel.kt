package com.shashi.openweathershiva.ui.weatherlatlon

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.shashi.openweathershiva.data.network.NoConnectivityException
import com.shashi.openweathershiva.data.network.Resource
import com.shashi.openweathershiva.data.repository.WeatherRepository
import com.shashi.openweathershiva.data.responses.WeatherLatLonResponse
import com.shashi.openweathershiva.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class WeatherLatLonViewModel @Inject constructor(
    private val repository: WeatherRepository
) : BaseViewModel(repository) {

    private val _weatherLatLonResponse: MutableStateFlow<Resource<WeatherLatLonResponse>> =
        MutableStateFlow(Resource.Loading)
    val weatherLatLonResponse: StateFlow<Resource<WeatherLatLonResponse>> = _weatherLatLonResponse

    // Get latitude longitude Api
    fun searchWeatherLatLon(
        latitude: Double, longitude: Double
    ) = viewModelScope.launch {
        _weatherLatLonResponse.value = Resource.Loading

        Log.i("shashil", "searchWeatherLatLon: ")
        repository.searchWeatherLatLon(latitude, longitude)
            .catch { e ->
                if (e is NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    _weatherLatLonResponse.value = Resource.Error(e.message)

                } else if (e is HttpException) {
                    val exception: HttpException = e
                    when (exception.code()) {
                        401 -> {
                            _weatherLatLonResponse.value =
                                Resource.Error(exception.code().toString())
                        }
                        else -> {
                            _weatherLatLonResponse.value = Resource.Error(e.message.toString())
                        }
                    }
                }
            }.collect { data ->
                _weatherLatLonResponse.value = Resource.Success(data)
            }
    }
}