package com.em.guesstheflag.domain

import android.content.Context
import com.em.guesstheflag.core.Resource
import com.em.guesstheflag.data.model.Country

interface Repository {
    suspend fun getAllRemoteCountries(): Resource<List<Country>>
    suspend fun getAllCountries(context: Context): Resource<List<Country>>
}