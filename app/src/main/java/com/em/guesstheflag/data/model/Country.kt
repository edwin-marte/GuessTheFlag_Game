package com.em.guesstheflag.data.model

import com.squareup.moshi.Json

data class Country(
    @Json(name = "name")
    val name: String = "",
    @Json(name = "image")
    val image: String = ""
)