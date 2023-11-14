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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.databinding.FragmentThomannConversationsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationsAdapter
import com.dauma.grokimkartu.ui.main.adapters.ThomannConversationData
import com.dauma.grokimkartu.viewmodels.main.ThomannConversationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ThomannConversationsFragment : Fragment() {

    private val thomannConversationsViewModel by viewModels<ThomannConversationsViewModel>()
    private var isViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentThomannConversationsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ThomannConversationsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThomannConversationsBinding.inflate(inflater, container, false)
        binding.model = thomannConversationsViewModel
        val view = binding.root
        setupOnClickers()
        setupObservers()
        isViewSetup = false

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            thomannConversationsViewModel.reload()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    thomannConversationsViewModel.uiState.collect {
                        val thomannConversationData = it.conversations.map { c -> ThomannConversationData(c) }
                        if (isViewSetup == false) {
                            setupThomannConversationsRecyclerView(thomannConversationData)
                        } else {
                            val conversationsAdapter = binding.thomannConversationsRecyclerView.adapter as? ConversationsAdapter
                            conversationsAdapter?.conversationsListData?.clear()
                            conversationsAdapter?.conversationsListData?.addAll(thomannConversationData)
                            binding.thomannConversationsRecyclerView.adapter?.notifyDataSetChanged()
                        }
                        if (binding.swipeRefreshLayout.isRefreshing) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    private fun setupThomannConversationsRecyclerView(conversations: List<ThomannConversationData>) {
        binding.thomannConversationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannConversationsRecyclerView.adapter = ConversationsAdapter(
            context = requireContext(),
            conversationsListData = conversations.toMutableList(),
            utils = utils,
            onItemClicked = { thomannId, name ->
                findNavController().navigate(ConversationsFragmentDirections.actionConversationsFragmentToConversationFragment2(
                    userId = -1,
                    thomannId = thomannId,
                    userName = name
                ))
            }
        )
        isViewSetup = true
    }
}