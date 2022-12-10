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
import com.dauma.grokimkartu.databinding.FragmentPlayersFilterBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.PlayersFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayersFilterFragment : Fragment() {
    private val playersFilterViewModel by viewModels<PlayersFilterViewModel>()
    private var dialogsManager: DialogsManager? = null
    private var isDialogShown: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentPlayersFilterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "PlayersFilterFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayersFilterBinding.inflate(inflater, container, false)
        binding.model = playersFilterViewModel
        val view = binding.root
        setupObservers()
        setupOnClickListeners()

        playersFilterViewModel.viewIsReady()
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
        playersFilterViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            if (isDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isDialogShown = false
            } else {
                this.findNavController().popBackStack()
            }
        })
        playersFilterViewModel.city.observe(viewLifecycleOwner, EventObserver { codeValues ->
            this.isDialogShown = true
            this.dialogsManager?.let { manager ->
                val pickableCitiesAsCodeValues = playersFilterViewModel
                    .getPlayersFilterForm()
                    .filteredPickableCities
                    .map { pc -> pc.toCodeValue() }

                manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                    title = getString(R.string.profile_edit_city),
                    codeValues = pickableCitiesAsCodeValues,
                    onSearchValueChanged = { value ->
                        this.playersFilterViewModel.searchCity(value) {
                            val pickableCitiesAsCodeValues = playersFilterViewModel
                                .getPlayersFilterForm()
                                .filteredPickableCities
                                .map { pc -> pc.toCodeValue() }
                            manager.setCodeValues(pickableCitiesAsCodeValues)
                        }
                    },
                    onCodeValueClicked = { code ->
                        val id = code.toIntOrNull()
                        if (id != null) {
//                            this.playersFilterViewModel.citySelected(id)
                            manager.hideBottomDialog()
                            this.isDialogShown = false
                        }
                    },
                    onCancelClicked = {}
                ))
            }
        })
        playersFilterViewModel.instrument.observe(viewLifecycleOwner, EventObserver { codeValues ->
            this.isDialogShown = true
            this.dialogsManager?.let { manager ->
                val pickableInstrumentsAsCodeValues = playersFilterViewModel
                    .getPlayersFilterForm()
                    .filteredPickableInstruments
                    .map { pi -> pi.toCodeValue() }

                manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                    title = getString(R.string.profile_edit_instrument),
                    codeValues = pickableInstrumentsAsCodeValues,
                    onSearchValueChanged = { value ->
                        this.playersFilterViewModel.searchInstrument(value) {
                            val pickableInstrumentsAsCodeValues = playersFilterViewModel
                                .getPlayersFilterForm()
                                .filteredPickableInstruments
                                .map { pi -> pi.toCodeValue() }
                            manager.setCodeValues(pickableInstrumentsAsCodeValues)
                        }
                    },
                    onCodeValueClicked = { code ->
                        val id = code.toIntOrNull()
                        if (id != null) {
//                            this.playersFilterViewModel.instrumentSelected(id)
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
        binding.playersFilterHeaderViewElement.setOnBackClick {
            playersFilterViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersFilterViewModel.backClicked()
        }

        binding.cityInputEditText.setOnClickListener {
            playersFilterViewModel.cityClicked()
        }

        binding.instrumentInputEditText.setOnClickListener {
            playersFilterViewModel.instrumentClicked()
        }
    }
}