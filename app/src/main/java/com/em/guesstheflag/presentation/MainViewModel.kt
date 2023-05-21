package com.em.guesstheflag.presentation

import android.content.Context
import android.media.MediaPlayer
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.em.guesstheflag.R
import com.em.guesstheflag.core.Resource
import com.em.guesstheflag.data.model.Country
import com.em.guesstheflag.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private var volumeOff = false
    var tap: MediaPlayer = MediaPlayer()
    var success: MediaPlayer = MediaPlayer()
    var failure: MediaPlayer = MediaPlayer()

    var countriesData = listOf<Country>()
    var countryToGuess: Country = Country()

    fun fetchCountries(context: Context) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(repository.getAllCountries(context))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun setupMediaPlayers(context: Context) {
        tap = MediaPlayer.create(context, R.raw.tap_sound)
        success = MediaPlayer.create(context, R.raw.success)
        failure = MediaPlayer.create(context, R.raw.failure)
    }

    private fun unmuteAll() {
        tap.setVolume(1f, 1f)
        success.setVolume(1f, 1f)
        failure.setVolume(1f, 1f)
    }

    private fun muteAll() {
        tap.setVolume(0f, 0f)
        success.setVolume(0f, 0f)
        failure.setVolume(0f, 0f)
    }

    fun setupVolume(button: ImageButton) {
        if (volumeOff) {
            button.setImageResource(R.drawable.volume_off)
            muteAll()
        } else {
            button.setImageResource(R.drawable.volume)
            unmuteAll()
        }
    }

    fun manageVolume(button: ImageButton) {
        volumeOff = if (volumeOff) {
            button.setImageResource(R.drawable.volume)
            unmuteAll()
            false
        } else {
            button.setImageResource(R.drawable.volume_off)
            muteAll()
            true
        }
    }

    fun selectRandomCountryWithOptions(countries: List<Country>): List<Country> {
        return countries.asSequence().shuffled().distinct().take(4).toList()
    }
}