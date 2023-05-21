package com.em.guesstheflag.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.em.guesstheflag.R
import com.em.guesstheflag.core.CheckNetwork
import com.em.guesstheflag.core.NetworkDialog
import com.em.guesstheflag.databinding.FragmentMenuBinding
import com.em.guesstheflag.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_menu, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInternetConnection()
        viewModel.setupMediaPlayers(requireContext())

        val prefs = requireActivity().getSharedPreferences(
            resources.getString(R.string.prefs_file), Context.MODE_PRIVATE
        )

        val highScore = prefs.getString("highScore", null)
        if (highScore == null) {
            val sPrefs = requireActivity().getSharedPreferences(
                resources.getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()
            sPrefs.putString("highScore", "0")
            sPrefs.apply()
        }

        if (highScore != null) {
            binding.bestScoreText.text = highScore
        }

        buttonActions()
        viewModel.setupVolume(binding.volumeButton)
    }

    private fun buttonActions() {
        binding.playGameButton.setOnClickListener {
            viewModel.tap.start()
            if (checkInternetConnection()) return@setOnClickListener
            onPlayButtonClick()
        }

        binding.volumeButton.setOnClickListener {
            viewModel.tap.start()
            viewModel.manageVolume(binding.volumeButton)
        }

        binding.exitButton.setOnClickListener {
            viewModel.tap.start()
            closeGame()
        }
    }

    private fun closeGame() {
        activity?.finish()
        exitProcess(0)
    }

    private fun onPlayButtonClick() {
        findNavController().navigate(
            R.id.action_menuFragment_to_gameFragment
        )
    }

    private fun checkInternetConnection(): Boolean {
        if (!CheckNetwork.isInternetAvailable(requireContext())) {
            NetworkDialog.show(activity as AppCompatActivity)
            return true
        }
        return false
    }
}