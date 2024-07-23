package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.util.TypedValue
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
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentMyThomannsBinding
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import com.dauma.grokimkartu.ui.main.adapters.ThomannListAdapter
import com.dauma.grokimkartu.viewmodels.main.MyThomannsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyThomannsFragment : Fragment() {
    private val myThomannsViewModel by viewModels<MyThomannsViewModel>()
    private var isRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentMyThomannsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "MyThomannsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyThomannsBinding.inflate(inflater, container, false)
        binding.model = myThomannsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isRecyclerViewSetup = false
        setupOnClickers()
        setupObservers()

        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipe_to_refresh_progress_spinner_color, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            myThomannsViewModel.reload()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myThomannsViewModel.uiState.collect {
                        val data = getAllThomannsFromPages(it.thomannsPages)
                        if (!isRecyclerViewSetup) {
                            setupRecyclerView()
                        }
                        reloadRecyclerViewWithNewData(data)
                        if (binding.swipeRefreshLayout.isRefreshing) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        if (it.isThomannDetailsStarted != -1) {
                            val args = Bundle()
                            args.putInt("thomannId", it.isThomannDetailsStarted)
                            findNavController().navigate(R.id.action_thomannFragment_to_thomannDetailsFragment, args)
                        }
                    }
                }
            }
        }
    }

    private fun getAllThomannsFromPages(pages: List<ThomannsPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            page.thomanns?.let {
                data.addAll(it)
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(DummyCell())
        }
        return data
    }

    private fun setupRecyclerView() {
        binding.myThomannsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.myThomannsRecyclerView.adapter = ThomannListAdapter(
            context = requireContext(),
            utils = utils,
            onItemClicked = { thomannId -> this.myThomannsViewModel.thomannItemClicked(thomannId) },
            loadNextPage = { this.myThomannsViewModel.loadNextThomannsPage() })
        isRecyclerViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.myThomannsRecyclerView.adapter
        if (adapter is ThomannListAdapter) {
            adapter.data = newData
        }
    }
}