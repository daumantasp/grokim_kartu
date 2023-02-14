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
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.BottomDialogDatePickerData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.ThomannsFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThomannsFilterFragment : Fragment() {
    private val thomannsFilterViewModel by viewModels<ThomannsFilterViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils

    private var _binding: FragmentThomannsFilterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThomannsFilterBinding.inflate(inflater, container, false)
        binding.model = thomannsFilterViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupOnClickListeners()
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
        thomannsFilterViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
        thomannsFilterViewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is ThomannsFilterViewModel.UiState.Form -> showForm()
                is ThomannsFilterViewModel.UiState.City -> showCitiesPicker()
                is ThomannsFilterViewModel.UiState.ValidUntil -> showValidUntilPicker(
                    currentDate = it.currentDate,
                    minDate = it.minDateTime,
                    maxDate = it.maxDateTime,
                    isSaveButtonEnabled = it.isSaveButtonEnabled
                )
            }
        }
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
        binding.validUntilInputEditText.setOnClickListener {
            thomannsFilterViewModel.validUntilClicked()
        }
        binding.applyFilterButton.setOnClick {
            thomannsFilterViewModel.applyFilter()
        }
        binding.clearFilterButton.setOnClick {
            thomannsFilterViewModel.clearFilter()
        }
    }

    private fun showCitiesPicker() {
        dialogsManager?.let { manager ->
            val pickableCitiesAsCodeValues = thomannsFilterViewModel
                .getThomannsFilterForm()
                .filteredPickableCities
                .map { pc -> pc.toCodeValue() }

            manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                title = getString(R.string.thomanns_filter_city),
                codeValues = pickableCitiesAsCodeValues,
                onSearchValueChanged = { value ->
                    thomannsFilterViewModel.searchCity(value) {
                        val filteredPickableCitiesAsCodeValues = thomannsFilterViewModel
                            .getThomannsFilterForm()
                            .filteredPickableCities
                            .map { pc -> pc.toCodeValue() }
                        manager.setCodeValues(filteredPickableCitiesAsCodeValues)
                    }
                },
                onCodeValueClicked = { code ->
                    code.toIntOrNull()?.let {
                        thomannsFilterViewModel.citySelected(it)
                    }
                },
                onCancelClicked = { thomannsFilterViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showValidUntilPicker(
        currentDate: CustomDateTime,
        minDate: CustomDateTime,
        maxDate: CustomDateTime,
        isSaveButtonEnabled: Boolean
    ) {
        dialogsManager?.let { manager ->
            manager.showBottomDatePickerDialog(BottomDialogDatePickerData(
                title = getString(R.string.thomanns_filter_valid_until),
                selectedDate = currentDate,
                minDate = minDate,
                maxDate = maxDate,
                isSaveButtonEnabled = isSaveButtonEnabled,
                onSaveClicked = { selectedDate ->
                    thomannsFilterViewModel.validUntilSelected(selectedDate)
                },
                onSelectedDateChanged = { selectedDate ->
                    manager.enableBottomDialogSaveButton(true)
                },
                onCancelClicked = { thomannsFilterViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showForm() {
        dialogsManager?.hideBottomDialog()
    }

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
        }
    }
}