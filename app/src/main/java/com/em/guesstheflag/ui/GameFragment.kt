package com.em.guesstheflag.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.em.guesstheflag.R
import com.em.guesstheflag.core.CheckNetwork
import com.em.guesstheflag.core.NetworkDialog
import com.em.guesstheflag.core.Resource
import com.em.guesstheflag.databinding.FragmentGameBinding
import com.em.guesstheflag.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchCountries(requireContext()).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    progressBarVisible(true)
                }
                is Resource.Success -> {
                    progressBarVisible(false)
                    viewModel.countriesData = result.data
                    updateView()
                }
                is Resource.Failure -> {
                    progressBarVisible(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${ result.exception.message }",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        buttonActions()
        viewModel.setupVolume(binding.volumeButton)
    }

    private fun buttonActions() {
        binding.volumeButton.setOnClickListener {
            viewModel.tap.start()
            viewModel.manageVolume(binding.volumeButton)
        }

        binding.giveUpButton.setOnClickListener {
            binding.giveUpConfirmation.visibility = View.VISIBLE
            viewModel.tap.start()
            buttonsClickable(false)
        }

        binding.replayButton.setOnClickListener {
            viewModel.tap.start()
            replayGame()
        }

        binding.mainMenuButton.setOnClickListener {
            viewModel.tap.start()
            findNavController().navigateUp()
        }

        //Confirmation
        binding.yesButton.setOnClickListener {
            viewModel.failure.start()
            binding.giveUpConfirmation.visibility = View.GONE
            gameOver()
        }

        binding.cancelButton.setOnClickListener {
            binding.giveUpConfirmation.visibility = View.GONE
            viewModel.tap.start()
            buttonsClickable(true)
        }
    }

    private fun progressBarVisible(visible: Boolean) {
        if (visible) {
            binding.progressBar.visibility = View.VISIBLE
            binding.linearLayoutTop.visibility = View.GONE
            binding.linearLayoutCenter.visibility = View.GONE
            binding.giveUpButton.visibility = View.GONE
            binding.volumeButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.linearLayoutTop.visibility = View.VISIBLE
            binding.linearLayoutCenter.visibility = View.VISIBLE
            binding.giveUpButton.visibility = View.VISIBLE
            binding.volumeButton.visibility = View.VISIBLE
        }
    }

    private fun buttonsClickable(value: Boolean) {
        binding.buttonOption1.isClickable = value
        binding.buttonOption2.isClickable = value
        binding.buttonOption3.isClickable = value
        binding.buttonOption4.isClickable = value
        binding.volumeButton.isClickable = value
        binding.giveUpButton.isClickable = value
    }

    private fun gameOver() {
        buttonsClickable(false)
        binding.gameOverPanel.visibility = View.VISIBLE
        binding.finalScoreText.text = binding.scoreText.text
        binding.correctAnswerText.text = viewModel.countryToGuess.name

        val prefs = requireActivity().getSharedPreferences(
            resources.getString(R.string.prefs_file), MODE_PRIVATE
        )

        val highScore = prefs.getString("highScore", null)
        if (highScore != null) {
            val currentScore = binding.scoreText.text.toString()
            if (highScore.toInt() < currentScore.toInt()) {
                binding.newMaxScoreText.visibility = View.VISIBLE
                val sPrefs = requireActivity().getSharedPreferences(
                    resources.getString(R.string.prefs_file),
                    MODE_PRIVATE
                ).edit()
                sPrefs.putString("highScore", currentScore)
                sPrefs.apply()
            } else {
                binding.newMaxScoreText.visibility = View.GONE
            }
        }
    }

    private fun replayGame() {
        if (checkInternetConnection()) return
        binding.gameOverPanel.visibility = View.GONE
        buttonsClickable(true)
        binding.finalScoreText.text = "0"
        binding.scoreText.text = "0"
        updateView()
    }

    private fun updateView() {
        val countries = viewModel.countriesData

        val options = viewModel.selectRandomCountryWithOptions(countries)
        if (options.isEmpty()) return
        viewModel.countryToGuess = options.random()

        val countryToGuess = viewModel.countryToGuess

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        fun ImageView.loadUrl(url: String) {
            val imageLoader = ImageLoader.Builder(this.context)
                .componentRegistry { add(SvgDecoder(this@loadUrl.context)) }
                .build()

            val request = ImageRequest.Builder(this.context)
                .crossfade(true)
                .crossfade(500)
                .placeholder(circularProgressDrawable)
                .data(url)
                .target(this)
                .build()

            imageLoader.enqueue(request)
        }

        binding.imageView.loadUrl(countryToGuess.image)

        val buttons = listOf(
            binding.buttonOption1,
            binding.buttonOption2,
            binding.buttonOption3,
            binding.buttonOption4
        )

        for ((index, button) in buttons.withIndex()) {
            val currentCountry = options[index]
            val currentCountryName = currentCountry.name
            button.text = currentCountryName
            button.setOnClickListener {
                if (checkInternetConnection()) return@setOnClickListener
                if (currentCountryName == countryToGuess.name) {
                    val score = binding.scoreText.text.toString().toInt()
                    binding.scoreText.text = score.plus(1).toString()
                    viewModel.success.start()
                    updateView()
                } else {
                    viewModel.failure.start()
                    gameOver()
                }
            }
        }
    }

    private fun checkInternetConnection(): Boolean {
        if (!CheckNetwork.isInternetAvailable(requireContext())) {
            NetworkDialog.show(activity as AppCompatActivity)
            return true
        }
        return false
    }
}