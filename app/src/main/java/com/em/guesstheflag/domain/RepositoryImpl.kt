package com.em.guesstheflag.domain

import android.content.Context
import com.em.guesstheflag.core.Resource
import com.em.guesstheflag.data.local.LocalClient
import com.em.guesstheflag.data.model.Country
import com.em.guesstheflag.data.remote.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class RepositoryImpl @Inject constructor(): Repository {
    override suspend fun getAllRemoteCountries(): Resource<List<Country>> {
        return Resource.Success(RetrofitClient.webService.getAllCountries())
    }

    override suspend fun getAllCountries(context: Context): Resource<List<Country>> {
        val localClient = LocalClient(context)
        val list: List<Country> = Gson().fromJson(localClient.jsonData, object : TypeToken<List<Country>>(){}.type)
        return Resource.Success(list)
    }
}