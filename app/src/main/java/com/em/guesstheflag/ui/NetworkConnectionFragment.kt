package com.em.guesstheflag.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.em.guesstheflag.R
import com.em.guesstheflag.core.CheckNetwork
import com.em.guesstheflag.databinding.FragmentNetworkConnectionBinding

class NetworkConnectionFragment : DialogFragment() {
    private lateinit var binding: FragmentNetworkConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_network_connection, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryButton.setOnClickListener {
            if (!CheckNetwork.isInternetAvailable(requireContext())) return@setOnClickListener
            requireDialog().dismiss()
        }
    }
}
