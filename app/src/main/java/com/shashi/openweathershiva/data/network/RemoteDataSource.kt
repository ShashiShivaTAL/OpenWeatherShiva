package com.shashi.openweathershiva.data.network

import android.content.Context
import androidx.viewbinding.BuildConfig
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.shashi.openweathershiva.BuildConfig.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteDataSource @Inject constructor() {

    companion object {
        const val FINAL_URL = BASE_URL
    }

    fun <Api> buildApi(
        api: Class<Api>,
        context: Context,
    ): Api {
        // val authenticator = TokenAuthenticator(context, buildTokenApi(context))
        return Retrofit.Builder()
            .baseUrl(FINAL_URL)
            // .client(getRetrofitClient(authenticator,context))
            .client(getRetrofitClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }


    private fun buildTokenApi(context: Context): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getRetrofitClient(context = context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }


    // private fun getRetrofitClient(authenticator: Authenticator? = null, context: Context): OkHttpClient {
    private fun getRetrofitClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(NetworkConnectionInterceptor(context))
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
            .addInterceptor { chain ->


                    chain.proceed(chain.request().newBuilder().also {
                    }.build())

            }
            .also { client ->
                //  authenticator?.let { client.authenticator(it) }
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
               }
            }.build()
    }
}