package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentPrivateConversationsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationAdapter
import com.dauma.grokimkartu.ui.main.adapters.ConversationsAdapter
import com.dauma.grokimkartu.ui.main.adapters.PrivateConversationData
import com.dauma.grokimkartu.viewmodels.main.PrivateConversationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrivateConversationsFragment : Fragment() {
    private val privateConversationsViewModel by viewModels<PrivateConversationsViewModel>()
    private var isViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentPrivateConversationsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "PrivateConversationsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivateConversationsBinding.inflate(inflater, container, false)
        binding.model = privateConversationsViewModel
        val view = binding.root
        setupObservers()
        isViewSetup = false

        binding.swipeRefreshLayout.setOnRefreshListener {
            privateConversationsViewModel.reload()
        }

        privateConversationsViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        privateConversationsViewModel.viewIsDiscarded()
    }

    private fun setupObservers() {
        privateConversationsViewModel.privateConversations.observe(viewLifecycleOwner, {
            val privateConversationData = it.map { c -> PrivateConversationData(c) }
            if (isViewSetup == false) {
                setupPrivateConversationsRecyclerView(privateConversationData)
            } else {
                val conversationsAdapter = binding.privateConversationsRecyclerView.adapter as? ConversationAdapter
                conversationsAdapter?.conversation?.clear()
                conversationsAdapter?.conversation?.addAll(privateConversationData)
                binding.privateConversationsRecyclerView.adapter?.notifyDataSetChanged()
            }
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
        privateConversationsViewModel.message.observe(viewLifecycleOwner, EventObserver { userData ->
            val args = Bundle()
            args.putInt("userId", userData[0] as Int)
            args.putString("userName", userData[1] as String)
            this.findNavController().navigate(R.id.action_conversationsFragment_to_conversationFragment2, args)
        })
    }

    private fun setupPrivateConversationsRecyclerView(conversations: List<PrivateConversationData>) {
        binding.privateConversationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.privateConversationsRecyclerView.adapter = ConversationsAdapter(
            context = requireContext(),
            conversationsListData = conversations.toMutableList(),
            utils = utils,
            onItemClicked = { userId, name ->
                this.privateConversationsViewModel.conversationClicked(userId, name)
            }
        )
        isViewSetup = true
    }
}