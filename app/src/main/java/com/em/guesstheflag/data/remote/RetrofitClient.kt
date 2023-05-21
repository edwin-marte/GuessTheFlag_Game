package com.em.guesstheflag.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val webService: WebService by lazy {
        Retrofit.Builder().baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
            .create(WebService::class.java)
    }
}