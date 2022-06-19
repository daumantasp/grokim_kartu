package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentHomeBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.CustomNavigator
import com.dauma.grokimkartu.ui.StatusBarManager
import com.dauma.grokimkartu.ui.StatusBarTheme
import com.dauma.grokimkartu.viewmodels.main.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private var customNavigator: CustomNavigator? = null
    private var statusBarManager: StatusBarManager? = null
    private var isUserIconGot: Boolean = false
    private var isUserProfileGot: Boolean = false
    private var userInitials: String = ""
    private var userIcon: Bitmap? = null
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
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.model = homeViewModel
        val view = binding.root
        statusBarManager?.changeStatusBarTheme(StatusBarTheme.MAIN)
        setupObservers()

        binding.homeHeaderViewElement.setOnInitialsOrIconClick {
            homeViewModel.userIconClicked()
        }
        binding.homeHeaderViewElement.showIconLoading(true)

        binding.playersCardViewElement.setOnClick {
            homeViewModel.playersClicked()
        }
        binding.thomannCardViewElement.setOnClick {
            homeViewModel.thomannClicked()
        }

        homeViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        customNavigator = null
        statusBarManager = null
    }

    private fun setupObservers() {
        homeViewModel.name.observe(viewLifecycleOwner, { name ->
            binding.homeHeaderViewElement.setTitle(name ?: "")
            binding.homeHeaderViewElement.showIconLoading(true)
            this.userInitials = utils.stringUtils.getInitials(name ?: "")
            this.isUserProfileGot = true
            this.setPhotoOrInitialsInHeaderIfPossible()
        })
        homeViewModel.userIcon.observe(viewLifecycleOwner, {
            binding.homeHeaderViewElement.showIconLoading(true)
            this.userIcon = it
            this.isUserIconGot = true
            this.setPhotoOrInitialsInHeaderIfPossible()
        })
        homeViewModel.unreadCount.observe(viewLifecycleOwner, { unreadCount ->
            val unreadCountNotNUll = unreadCount ?: 0
            binding.homeHeaderViewElement.setUnreadNotificationsCount(unreadCountNotNUll.toString())
            binding.homeHeaderViewElement.showUnreadNotificationsCount(unreadCountNotNUll > 0)
        })
        homeViewModel.navigateToProfile.observe(viewLifecycleOwner, EventObserver {
            customNavigator?.navigateToProfile()
        })
        homeViewModel.navigateToPlayers.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_homeFragment_to_playersFragment)
        })
        homeViewModel.navigateToThomann.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_homeFragment_to_thomannFragment)
        })
    }

    private fun setPhotoOrInitialsInHeaderIfPossible() {
        if (isUserProfileGot == true && isUserIconGot == true) {
            binding.homeHeaderViewElement.showIconLoading(false)
            if (userIcon != null) {
                binding.homeHeaderViewElement.setPhotoIcon(userIcon!!)
            } else {
                binding.homeHeaderViewElement.setInitials(userInitials)
            }
        }
    }
}