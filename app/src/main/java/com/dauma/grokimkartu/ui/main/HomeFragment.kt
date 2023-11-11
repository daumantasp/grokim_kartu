package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentHomeBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.CustomNavigator
import com.dauma.grokimkartu.ui.StatusBarManager
import com.dauma.grokimkartu.ui.StatusBarTheme
import com.dauma.grokimkartu.viewmodels.main.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private var customNavigator: CustomNavigator? = null
    private var statusBarManager: StatusBarManager? = null
    @Inject lateinit var utils: Utils

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "HomeFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        customNavigator = context as? CustomNavigator
        statusBarManager = context as? StatusBarManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.model = homeViewModel
        val view = binding.root
        setupOnClickListeners()
        setupObservers()

        statusBarManager?.changeStatusBarTheme(StatusBarTheme.MAIN)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        customNavigator = null
        statusBarManager = null
    }

    private fun setupOnClickListeners() {
        binding.homeHeaderViewElement.setOnInitialsOrIconClick {
            customNavigator?.navigateToProfile()
        }
        binding.homeHeaderViewElement.setOnNotificationsClick {
            homeViewModel.notifications()
        }
        binding.playersCardViewElement.setOnClick {
            homeViewModel.players()
        }
        binding.thomannCardViewElement.setOnClick {
            homeViewModel.thomann()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.uiState.collect {
                        binding.homeHeaderViewElement.setTitle(it.name ?: "")
                        it.userIcon?.let { userIcon ->
                            binding.homeHeaderViewElement.setPhotoIcon(userIcon)
                        } ?: run {
                            val userInitials = utils.stringUtils.getInitials(it.name ?: "")
                            binding.homeHeaderViewElement.setInitials(userInitials)
                        }
                        val unreadCountNotNull = it.unreadCount ?: 0
                        binding.homeHeaderViewElement.setUnreadNotificationsCount(unreadCountNotNull.toString())
                        binding.homeHeaderViewElement.showUnreadNotificationsCount(
                            unreadCountNotNull > 0
                        )
                        if (it.isNotificationsStarted) {
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNotificationsFragment())
                            homeViewModel.notificationsStarted()
                        } else if (it.isPlayersStarted) {
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPlayersFragment())
                            homeViewModel.playersStarted()
                        } else if (it.isThomannStarted) {
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToThomannFragment())
                            homeViewModel.thomannStarted()
                        }
                    }
                }
            }
        }
    }
}