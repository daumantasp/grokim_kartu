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
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.main.ThomannDetailsViewModel
import com.dauma.grokimkartu.databinding.FragmentThomannDetailsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.*
import com.dauma.grokimkartu.viewmodels.main.ThomannDetails
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThomannDetailsFragment : Fragment() {
    private val thomannDetailsViewModel by viewModels<ThomannDetailsViewModel>()
    private var isDetailsRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils
    private val recyclerViewData: MutableList<ThomannDetailsListData> = mutableListOf()

    private var _binding: FragmentThomannDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentThomannDetailsBinding.inflate(inflater, container, false)
        binding.model = thomannDetailsViewModel
        val view = binding.root
        setupObservers()
        isDetailsRecyclerViewSetup = false

        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            thomannDetailsViewModel.loadDetails()
        }

        binding.thomannDetailsHeaderViewElement.setOnBackClick {
            thomannDetailsViewModel.backClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        thomannDetailsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        thomannDetailsViewModel.detailsLoaded.observe(viewLifecycleOwner) {
            setupRecyclerViewData(it)
            if (isDetailsRecyclerViewSetup == false) {
                setupDetailsRecyclerView()
            } else {
                binding.thomannDetailsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
        thomannDetailsViewModel.userDetails.observe(viewLifecycleOwner, EventObserver {
            val args = Bundle()
            args.putString("userId", it)
            this.findNavController().navigate(R.id.action_thomannDetailsFragment_to_playerDetailsFragment, args)
        })
    }

    private fun setupDetailsRecyclerView() {
        binding.thomannDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannDetailsRecyclerView.adapter = ThomannDetailsAdapter(
            context = requireContext(),
            data = recyclerViewData,
            utils = utils,
            onItemClicked = { this.thomannDetailsViewModel.userClicked(it) },
            onLeaveClicked = { this.thomannDetailsViewModel.leaveClicked() },
            onKickClicked = { userId -> this.thomannDetailsViewModel.kickClicked(userId) }
        )
        isDetailsRecyclerViewSetup = true
    }

    private fun setupRecyclerViewData(details: ThomannDetails)  {
        recyclerViewData.clear()
        if (details.photo != null) {
            recyclerViewData.add(ThomannDetailsListPhotoData(details.name, details.photo, details.isLocked))
        }
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_name), details.name))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_city), details.city))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_creation_date), details.creationDate))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_valid_until), details.validUntilDate))
        for (user in details.users) {
            recyclerViewData.add(ThomannDetailsListUserData(user))
        }
        if (details.isJoinable) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_join), details.onJoinButtonClick))
        }
        if (details.isLockable) {
            val isLocked = details.isLocked
            val title = if (isLocked) getString(R.string.thomann_details_unlock) else getString(R.string.thomann_details_lock)
            recyclerViewData.add(ThomannDetailsListButtonData(title, { details.onLockButtonClick(isLocked == false) }))
        }
        if (details.isCancelable) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_cancel), details.onCancelButtonClick))
        }
    }
}