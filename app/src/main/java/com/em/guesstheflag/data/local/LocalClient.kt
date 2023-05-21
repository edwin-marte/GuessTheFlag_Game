package com.em.guesstheflag.data.local

import android.content.Context
import com.em.guesstheflag.R

class LocalClient(context: Context) {
    val jsonData = context.resources.openRawResource(
        R.raw.countries
    ).bufferedReader().use { it.readText() }
}