package com.dauma.grokimkartu.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentSplashBinding
import com.dauma.grokimkartu.general.EventObserver
import com.dauma.grokimkartu.viewmodels.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private val splashViewModel by viewModels<SplashViewModel>()

    private var _binding: FragmentSplashBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        binding.model = splashViewModel
        val view = binding.root

        splashViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        })

        splashViewModel.navigateToPlayers.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigate(R.id.action_splashFragment_to_playersFragment)
            }
        })

        splashViewModel.splashCompleted()
        return view
    }
}