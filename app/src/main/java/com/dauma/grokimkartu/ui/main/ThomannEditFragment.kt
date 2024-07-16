package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentThomannEditBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.BottomDialogDatePickerData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.ui.YesNoDialogData
import com.dauma.grokimkartu.viewmodels.main.ThomannEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ThomannEditFragment : Fragment() {
    private val thomannEditViewModel by viewModels<ThomannEditViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils

    private var _binding: FragmentThomannEditBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThomannEditBinding.inflate(inflater, container, false)
        binding.model = thomannEditViewModel
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

    private fun setupOnClickListeners() {
        binding.thomannEditHeaderViewElement.setOnBackClick {
            thomannEditViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannEditViewModel.back()
        }
        binding.cityInputEditText.setOnClickListener {
            thomannEditViewModel.cityClicked()
        }
        binding.validUntilInputEditText.setOnClickListener {
            thomannEditViewModel.validUntilClicked()
        }
        binding.saveThomannButton.setOnClick {
            thomannEditViewModel.saveChanges()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    thomannEditViewModel.uiState.collect {
                        binding.saveThomannButton.showAnimation(it.isInProgress)
                        if (it.isCitySelectionStarted) {
                            showCitiesPicker()
                        } else if (it.isDateSelectionStarted && it.date != null) {
                            showValidUntilPicker(
                                currentDate = it.date.currentDate,
                                minDate = it.date.minDateTime,
                                maxDate = it.date.maxDateTime,
                                isSaveButtonEnabled = it.date.isSaveButtonEnabled
                            )
                        } else {
                            showForm()
                        }
                        if (it.isConfirmation) {
                            showBackConfirmationDialog()
                        } else if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showCitiesPicker() {
        dialogsManager?.let { manager ->
            val pickableCitiesAsCodeValues = thomannEditViewModel
                .thomannEditForm()
                .filteredPickableCities
                .map { pc -> pc.toCodeValue() }
            manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                title = getString(R.string.thomann_edit_city),
                codeValues = pickableCitiesAsCodeValues,
                onSearchValueChanged = { value ->
                    thomannEditViewModel.searchCity(value) {
                        val filteredPickableCitiesAsCodeValues = thomannEditViewModel
                            .thomannEditForm()
                            .filteredPickableCities
                            .map { pc -> pc.toCodeValue() }
                        manager.setCodeValues(filteredPickableCitiesAsCodeValues)
                    }
                },
                onCodeValueClicked = { code ->
                    code.toIntOrNull()?.let {
                        thomannEditViewModel.citySelected(it)
                    }
                },
                onCancelClicked = { thomannEditViewModel.cancelPickerClicked() }
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
                    thomannEditViewModel.validUntilSelected(selectedDate)
                },
                onSelectedDateChanged = { selectedDate ->
                    manager.enableBottomDialogSaveButton(true)
                },
                onCancelClicked = { thomannEditViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showBackConfirmationDialog() {
        dialogsManager?.showYesNoDialog(YesNoDialogData(
            text = getString(R.string.thomann_edit_navigate_back_confirmation_text),
            positiveText = getString(R.string.thomann_edit_navigate_back_confirmation_positive),
            negativeText = getString(R.string.thomann_edit_navigate_back_confirmation_negative),
            cancelable = true,
            onPositiveButtonClick = { thomannEditViewModel.back() },
            onNegativeButtonClick = { thomannEditViewModel.cancelBack() },
            onCancelClicked = { thomannEditViewModel.cancelBack() }
        ))
    }

    private fun showForm() {
        dialogsManager?.hideBottomDialog()
    }
}