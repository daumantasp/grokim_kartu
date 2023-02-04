package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.databinding.FragmentThomannConversationsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationsAdapter
import com.dauma.grokimkartu.ui.main.adapters.ThomannConversationData
import com.dauma.grokimkartu.viewmodels.main.ThomannConversationsViewModel
import dagger.hilt.android.AndroidEntryPoint
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
    ): View? {
        _binding = FragmentThomannConversationsBinding.inflate(inflater, container, false)
        binding.model = thomannConversationsViewModel
        val view = binding.root
        setupObservers()
        isViewSetup = false

        binding.swipeRefreshLayout.setOnRefreshListener {
            thomannConversationsViewModel.reload()
        }

        thomannConversationsViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        thomannConversationsViewModel.viewIsDiscarded()
    }

    private fun setupObservers() {
        thomannConversationsViewModel.thomannConversations.observe(viewLifecycleOwner, {
            val thomannConversationData = it.map { c -> ThomannConversationData(c) }
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
        })
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