package com.em.guesstheflag.data.remote

import com.em.guesstheflag.data.model.Country
import retrofit2.http.GET

interface WebService {
    @GET("all")
    suspend fun getAllCountries(): List<Country>
}