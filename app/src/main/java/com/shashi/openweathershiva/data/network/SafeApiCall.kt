package com.shashi.openweathershiva.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {

                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {

                    is HttpException -> {
                        Resource.Error( exception = throwable.response()?.errorBody().toString())
                    }
                    is IOException -> {
                        Resource.Error("Please check your network connection")
                    }

                    is Exception ->{
                        Resource.Error("Something went wrong")
                    }
                    else -> {
                        Resource.Error(throwable.message.toString())
                    }
                }
            }
        }
    }
}