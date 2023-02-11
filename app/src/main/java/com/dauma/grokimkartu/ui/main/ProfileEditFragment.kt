package com.dauma.grokimkartu.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileEditBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ProfileEditForm
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.ui.YesNoDialogData
import com.dauma.grokimkartu.viewmodels.main.ProfileEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private val profileEditViewModel by viewModels<ProfileEditViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils
    private var pickCaptureResult: ActivityResultLauncher<Intent>
    private var photoFile: File? = null

    private var _binding: FragmentProfileEditBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ProfileEditFragment"
    }

    init {
        pickCaptureResult = getPickCaptureResult()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.model = profileEditViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.profileEditHeaderViewElement.setOnBackClick {
            profileEditViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            profileEditViewModel.backClicked()
        }
        binding.selectPhotoButton.setOnClickListener {
            showPhotoChooser()
        }
        binding.cityInputEditText.setOnClickListener {
            profileEditViewModel.cityClicked()
        }
        binding.instrumentInputEditText.setOnClickListener {
            profileEditViewModel.instrumentClicked()
        }
        binding.saveChangesButton.setOnClick {
            binding.saveChangesButton.showAnimation(true)
            profileEditViewModel.saveChanges {
                binding.saveChangesButton.showAnimation(false)
            }
        }
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
        profileEditViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
        profileEditViewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                ProfileEditViewModel.UiState.FORM -> showForm()
                ProfileEditViewModel.UiState.CITY -> showCitiesPicker()
                ProfileEditViewModel.UiState.INSTRUMENT -> showInstrumentsPicker()
                ProfileEditViewModel.UiState.BACK_CONFIRMATION -> showBackConfirmationDialog()
                null -> {}
            }
        }
        profileEditViewModel.getProfileEditForm().addOnPropertyChangedCallback(onPhotoOrNameChanged())
    }

    private fun onPhotoOrNameChanged() : OnPropertyChangedCallback {
        return object : OnPropertyChangedCallback() {
            var isProfileLoaded = false
            var isPhotoLoaded = false
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                fun checkIfFullProfileLoaded() {
                    if (isProfileLoaded == true && isPhotoLoaded == true) {
                        val profileEditForm = sender as ProfileEditForm
                        profileEditForm.photo?.let {
                            binding.photoImageView.setImageBitmap(it)
                            binding.profileInitialsViewElement.visibility = View.GONE
                            binding.photoImageView.visibility = View.VISIBLE
                            binding.profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE
                        } ?: run {
                            val initials = utils.stringUtils.getInitials(profileEditForm.name)
                            binding.profileInitialsViewElement.setInitials(initials)
                            binding.photoImageView.visibility = View.GONE
                            binding.profileInitialsViewElement.visibility = View.VISIBLE
                            binding.profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE
                        }
                    }
                }
                if (propertyId == BR.photo) {
                    isPhotoLoaded = true
                    checkIfFullProfileLoaded()
                } else if (propertyId == BR.name) {
                    isProfileLoaded = true
                    checkIfFullProfileLoaded()
                }
            }
        }
    }

    private fun showCitiesPicker() {
        dialogsManager?.let { manager ->
            val pickableCitiesAsCodeValues = profileEditViewModel
                .getProfileEditForm()
                .filteredPickableCities
                .map { pc -> pc.toCodeValue() }
            manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                title = getString(R.string.profile_edit_city),
                codeValues = pickableCitiesAsCodeValues,
                onSearchValueChanged = { value ->
                    profileEditViewModel.searchCity(value) {
                        val filteredPickableCitiesAsCodeValues = profileEditViewModel
                            .getProfileEditForm()
                            .filteredPickableCities
                            .map { pc -> pc.toCodeValue() }
                        manager.setCodeValues(filteredPickableCitiesAsCodeValues)
                    }
                },
                onCodeValueClicked = { code ->
                    code.toIntOrNull()?.let {
                        profileEditViewModel.citySelected(it)
                    }
                },
                onCancelClicked = { profileEditViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showInstrumentsPicker() {
        dialogsManager?.let { manager ->
            val pickableInstrumentsAsCodeValues = profileEditViewModel
                .getProfileEditForm()
                .filteredPickableInstruments
                .map { pi -> pi.toCodeValue() }
            manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                title = getString(R.string.profile_edit_instrument),
                codeValues = pickableInstrumentsAsCodeValues,
                onSearchValueChanged = { value ->
                    profileEditViewModel.searchInstrument(value) {
                        val filteredPickableInstrumentsAsCodeValues = profileEditViewModel
                            .getProfileEditForm()
                            .filteredPickableInstruments
                            .map { pi -> pi.toCodeValue() }
                        manager.setCodeValues(filteredPickableInstrumentsAsCodeValues)
                    }
                },
                onCodeValueClicked = { code ->
                    code.toIntOrNull()?.let {
                        profileEditViewModel.instrumentSelected(it)
                    }
                },
                onCancelClicked = { profileEditViewModel.cancelPickerClicked() }
            ))
        }
    }

    private fun showBackConfirmationDialog() {
        dialogsManager?.showYesNoDialog(YesNoDialogData(
            text = getString(R.string.profile_edit_navigate_back_confirmation_text),
            positiveText = getString(R.string.profile_edit_navigate_back_confirmation_positive),
            negativeText = getString(R.string.profile_edit_navigate_back_confirmation_negative),
            cancelable = true,
            onPositiveButtonClick = { profileEditViewModel.backClicked() },
            onNegativeButtonClick = { profileEditViewModel.cancelBackClicked() },
            onCancelClicked = { profileEditViewModel.cancelBackClicked() }
        ))
    }

    private fun showForm() {
        dialogsManager?.hideBottomDialog()
    }

    private fun getPickCaptureResult() : ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val width = resources.getDimensionPixelSize(R.dimen.default_profile_photo_width)
                val height = resources.getDimensionPixelSize(R.dimen.default_profile_photo_height)

                val imageUri = result.data?.data
                if (imageUri != null) {
                    // this case will occur in case of picking image from the Gallery,
                    // but not when taking picture with a camera
                    val image = utils.imageUtils.getImageWithAuthority(imageUri, width, height)
                    if (image != null) {
                        profileEditViewModel.getProfileEditForm().photo = image
                    }
                } else {
                    // this case will occur when taking a picture with a camera
                    // read more at Android Apprentice book
                    val photoFile = photoFile ?: return@registerForActivityResult
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.dauma.grokimkartu.fileprovider",
                        photoFile)
                    requireContext().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    val image = utils.imageUtils.getImageWithAuthority(photoFile.absolutePath, width, height)
                    profileEditViewModel.getProfileEditForm().photo = image
                }
            }
        }
    }

    private fun showPhotoChooser() {
        val pickIntent: Intent? = if (canPick()) Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) else null
        var captureIntent: Intent? = null
        if (canCapture()) {
            photoFile = null
            try {
                photoFile = utils.imageUtils.createUniqueImageFile()
            } catch (ex: java.io.IOException) { }

            photoFile?.let { photoFile ->
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.dauma.grokimkartu.fileprovider",
                    photoFile
                )
                captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureIntent!!.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val intentActivities = requireContext().packageManager.queryIntentActivities(
                    captureIntent!!,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                intentActivities
                    .map { it.activityInfo.packageName }
                    .forEach { requireContext().grantUriPermission(it, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }
            }
        }

        if (pickIntent != null && captureIntent != null) {
            val chooser = Intent(Intent.ACTION_CHOOSER)
            chooser.putExtra(Intent.EXTRA_INTENT, pickIntent)
            chooser.putExtra(Intent.EXTRA_TITLE, getString(R.string.profile_edit_chooser_title))
            val intentArray = arrayOf(captureIntent)
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            pickCaptureResult.launch(chooser)
        } else if (pickIntent != null) {
            pickCaptureResult.launch(pickIntent)
        } else if (captureIntent != null) {
            pickCaptureResult.launch(captureIntent)
        }
    }

    private fun canPick() : Boolean {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        return pickIntent.resolveActivity(requireContext().packageManager) != null
    }

    private fun canCapture() : Boolean {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        return captureIntent.resolveActivity(requireContext().packageManager) != null
    }

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
        }
    }
}