package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentThomannsFilterBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.ThomannsFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThomannsFilterFragment : Fragment() {
    private val thomannsFilterViewModel by viewModels<ThomannsFilterViewModel>()
    private var dialogsManager: DialogsManager? = null
    private var isDialogShown: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentThomannsFilterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ThomannsFilterFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThomannsFilterBinding.inflate(inflater, container, false)
        binding.model = thomannsFilterViewModel
        val view = binding.root
        setupObservers()
        setupOnClickListeners()

        thomannsFilterViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        dialogsManager = null
    }

    private fun setupObservers() {
        thomannsFilterViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            if (isDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isDialogShown = false
            } else {
                this.findNavController().popBackStack()
            }
        })
        thomannsFilterViewModel.city.observe(viewLifecycleOwner, EventObserver { codeValues ->
            this.isDialogShown = true
            this.dialogsManager?.let { manager ->
                val pickableCitiesAsCodeValues = thomannsFilterViewModel
                    .getThomannsFilterForm()
                    .filteredPickableCities
                    .map { pc -> pc.toCodeValue() }

                manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                    title = getString(R.string.profile_edit_city),
                    codeValues = pickableCitiesAsCodeValues,
                    onSearchValueChanged = { value ->
                        this.thomannsFilterViewModel.searchCity(value) {
                            val pickableCitiesAsCodeValues = thomannsFilterViewModel
                                .getThomannsFilterForm()
                                .filteredPickableCities
                                .map { pc -> pc.toCodeValue() }
                            manager.setCodeValues(pickableCitiesAsCodeValues)
                        }
                    },
                    onCodeValueClicked = { code ->
                        val id = code.toIntOrNull()
                        if (id != null) {
                            this.thomannsFilterViewModel.citySelected(id)
                            manager.hideBottomDialog()
                            this.isDialogShown = false
                        }
                    },
                    onCancelClicked = {}
                ))
            }
        })
    }

    private fun setupOnClickListeners() {
        binding.thomannsFilterHeaderViewElement.setOnBackClick {
            thomannsFilterViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannsFilterViewModel.backClicked()
        }
        binding.cityInputEditText.setOnClickListener {
            thomannsFilterViewModel.cityClicked()
        }
//        binding.applyFilterButton.setOnClick(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                thomannsFilterViewModel.applyFilter()
//            }
//        })
//        binding.clearFilterButton.setOnClick(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                thomannsFilterViewModel.clearFilter()
//            }
//        })
    }
}