package com.dauma.grokimkartu.ui.main

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
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentThomannsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ThomannsPagerAdapter
import com.dauma.grokimkartu.viewmodels.main.ThomannsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ThomannsFragment : Fragment() {
    private val thomannsViewModel by viewModels<ThomannsViewModel>()
    @Inject lateinit var utils: Utils

    private var _binding: FragmentThomannsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThomannsBinding.inflate(inflater, container, false)
        binding.model = thomannsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
        setupOnClickers()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.thomannsHeaderViewElement.setOnBackClick {
            thomannsViewModel.back()
        }
        binding.thomannsHeaderViewElement.setOnRightTextClick {
            thomannsViewModel.filterClicked()
        }
        binding.createThomannItemButton.setOnClick {
            thomannsViewModel.createClicked()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    thomannsViewModel.uiState.collect {
                        if (it.isAllTabActive) {
                            binding.thomannsHeaderViewElement.showRightTextAsDisabled(false)
                            binding.thomannsHeaderViewElement.showRightTextAttentioner(it.isFilterApplied)
                        } else {
                            binding.thomannsHeaderViewElement.showRightTextAsDisabled(true)
                        }
                        if (it.isFilterStarted) {
                            findNavController().navigate(R.id.action_thomannFragment_to_thomannsFilterFragment)
                            thomannsViewModel.filterStarted()
                        } else if (it.isCreateStarted) {
                            findNavController().navigate(R.id.action_thomannFragment_to_thomannEditFragment)
                            thomannsViewModel.createStarted()
                        } else if (it.close)
                            findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupTabLayout() {
        val viewPager = binding.thomannsViewPager
        val tabLayout = binding.thomannsTabLayout

        val adapter = ThomannsPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        val tabTitles = arrayOf(
            requireContext().getString(R.string.thomanns_all_thomanns_title),
            requireContext().getString(R.string.thomanns_my_thomanns_title)
        )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    thomannsViewModel.tabSelected(it == 0)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}