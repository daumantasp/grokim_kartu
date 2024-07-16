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
import com.dauma.grokimkartu.databinding.FragmentPlayersFilterBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.PlayersFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayersFilterFragment : Fragment() {
    private val playersFilterViewModel by viewModels<PlayersFilterViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils

    private var _binding: FragmentPlayersFilterBinding? = null
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
        _binding = FragmentPlayersFilterBinding.inflate(inflater, container, false)
        binding.model = playersFilterViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListeners()
        setupObservers()
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
        binding.cityInputEditText.setOnClickListener {
            playersFilterViewModel.cityClicked()
        }
        binding.instrumentInputEditText.setOnClickListener {
            playersFilterViewModel.instrumentClicked()
        }
        binding.applyFilterButton.setOnClick {
            playersFilterViewModel.applyFilter()
        }
        binding.clearFilterButton.setOnClick {
            playersFilterViewModel.clearFilter()
        }
        binding.playersFilterHeaderViewElement.setOnBackClick {
            playersFilterViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersFilterViewModel.back()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    playersFilterViewModel.uiState.collect {
                        if (it.isCitySelectionStarted) {
                            showCitiesPicker()
                        } else if (it.isInstrumentSelectionStarted) {
                            showInstrumentsPicker()
                        } else {
                            showForm()
                        }
                        if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showCitiesPicker() {
        dialogsManager?.let { manager ->
            val pickableCitiesAsCodeValues = playersFilterViewModel
                .getPlayersFilterForm()
                .filteredPickableCities
                .map { pc -> pc.toCodeValue() }
            manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                title = getString(R.string.players_filter_city),
                codeValues = pickableCitiesAsCodeValues,
                onSearchValueChanged = { value ->
                    playersFilterViewModel.searchCity(value) {
                        val filteredPickableCitiesAsCodeValues = playersFilterViewModel
                            .getPlayersFilterForm()
                            .filteredPickableCities
                            .map { pc -> pc.toCodeValue() }
                        manager.setCodeValues(filteredPickableCitiesAsCodeValues)
                    }
                },
                onCodeValueClicked = { code ->
                    code.toIntOrNull()?.let {
                        playersFilterViewModel.citySelected(it)
                    }
                },
                onCancelClicked = { playersFilterViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showInstrumentsPicker() {
        dialogsManager?.let { manager ->
            val pickableInstrumentsAsCodeValues = playersFilterViewModel
                .getPlayersFilterForm()
                .filteredPickableInstruments
                .map { pi -> pi.toCodeValue() }
            manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                title = getString(R.string.players_filter_instrument),
                codeValues = pickableInstrumentsAsCodeValues,
                onSearchValueChanged = { value ->
                    playersFilterViewModel.searchInstrument(value) {
                        val filteredPickableInstrumentsAsCodeValues = playersFilterViewModel
                            .getPlayersFilterForm()
                            .filteredPickableInstruments
                            .map { pi -> pi.toCodeValue() }
                        manager.setCodeValues(filteredPickableInstrumentsAsCodeValues)
                    }
                },
                onCodeValueClicked = { code ->
                    code.toIntOrNull()?.let {
                        playersFilterViewModel.instrumentSelected(it)
                    }
                },
                onCancelClicked = { playersFilterViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showForm() {
        dialogsManager?.hideBottomDialog()
    }
}