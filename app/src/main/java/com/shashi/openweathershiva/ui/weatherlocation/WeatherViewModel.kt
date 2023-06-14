package com.shashi.openweathershiva.ui.weatherlocation

import androidx.lifecycle.viewModelScope
import com.shashi.openweathershiva.data.network.NoConnectivityException
import com.shashi.openweathershiva.data.network.Resource
import com.shashi.openweathershiva.data.repository.WeatherRepository
import com.shashi.openweathershiva.data.responses.WeatherSearchResponse
import com.shashi.openweathershiva.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : BaseViewModel(repository) {

    private val _weatherSearchResponse: MutableStateFlow<Resource<WeatherSearchResponse>> =
        MutableStateFlow(Resource.Loading)
    val weatherSearchResponse: StateFlow<Resource<WeatherSearchResponse>> = _weatherSearchResponse

    // Get latitude longitude Api
    fun weatherSearch(cityName: String) = viewModelScope.launch {
        _weatherSearchResponse.value = Resource.Loading

        repository.weatherSearch(cityName)
            .catch { e ->
                if (e is NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    _weatherSearchResponse.value = Resource.Error(e.message)

                } else if (e is HttpException) {
                    val exception: HttpException = e
                    when (exception.code()) {
                        401 -> {
                            _weatherSearchResponse.value =
                                Resource.Error(exception.code().toString())
                        }
                        else -> {
                            _weatherSearchResponse.value = Resource.Error(e.message.toString())
                        }
                    }
                }
            }.collect { data ->
                _weatherSearchResponse.value = Resource.Success(data)
            }
    }
}