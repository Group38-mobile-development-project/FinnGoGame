package com.example.gamestore.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://api.rawg.io/api/"
    private const val API_KEY = "3e3052ba07ce493c93f7cb3659a03030"

    private val apiKeyInterceptor = Interceptor { chain ->
        val original = chain.request()
        val originalUrl = original.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("key", API_KEY)
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        Log.d("ApiClient", "Fetching data from: $newUrl")

        chain.proceed(newRequest)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .build()

    val apiService: GameApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApi::class.java)
    }
}
