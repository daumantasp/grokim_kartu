package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentPlayersBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.PlayersListAdapter
import com.dauma.grokimkartu.ui.main.adapters.PlayersListData
import com.dauma.grokimkartu.viewmodels.main.PlayersViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// read more at https://medium.com/mobile-app-development-publication/injecting-viewmodel-with-dagger-hilt-54ca2e433865

@AndroidEntryPoint
class PlayersFragment : Fragment() {
    private val playersViewModel by viewModels<PlayersViewModel>()
    private var isPlayersRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentPlayersBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "PlayersFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayersBinding.inflate(inflater, container, false)
        binding.model = playersViewModel
        val view = binding.root
        setupObservers()

        binding.playersHeaderViewElement.setOnBackClick {
            playersViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersViewModel.backClicked()
        }

//        binding.playersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val firstItem = (binding.playersRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//                binding.playersHeaderViewElement.showShadow(firstItem > 0)
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        })

        playersViewModel.viewIsReady()
        return view
    }

    private fun setupObservers() {
        playersViewModel.playerDetails.observe(viewLifecycleOwner, EventObserver {
            val args = Bundle()
            args.putString("userId", it)
            this.findNavController().navigate(R.id.action_playersFragment_to_playerDetailsFragment, args)
        })
        playersViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        playersViewModel.playersListData.observe(viewLifecycleOwner, Observer {
                if (isPlayersRecyclerViewSetup == false) {
                    setupPlayersRecyclerView(it)
                } else {
                    binding.playersRecyclerView.adapter?.notifyDataSetChanged()
                }
            })
    }

    private fun setupPlayersRecyclerView(playersListData: List<PlayersListData>) {
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.playersRecyclerView.adapter = PlayersListAdapter(requireContext(), playersListData, utils) { userId ->
            this.playersViewModel.playerClicked(userId)
        }
    }
}