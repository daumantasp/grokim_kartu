package com.dauma.grokimkartu.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileEditBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.ProfileEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private val profileEditViewModel by viewModels<ProfileEditViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils
    private var galleryResult: ActivityResultLauncher<Intent>
    //    private var cameraResult: ActivityResultLauncher<Intent>
    private var isDialogShown: Boolean = false

    private var _binding: FragmentProfileEditBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ProfileEditFragment"
    }

    init {
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val width = resources.getDimensionPixelSize(R.dimen.default_profile_photo_width)
                    val height = resources.getDimensionPixelSize(R.dimen.default_profile_photo_height)
                    val image = utils.imageUtils.getImageWithAuthority(requireContext(), imageUri, width, height)
                    if (image != null) {
                        profileEditViewModel.getProfileEditForm().photo = image
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.model = profileEditViewModel
        val view = binding.root
        setupObservers()
        setupOnClickListeners()

        profileEditViewModel.viewIsReady()
        return view
    }

    private fun setupOnClickListeners() {
        binding.profileEditHeaderViewElement.setOnBackClick {
            profileEditViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            profileEditViewModel.backClicked()
        }

        binding.cityInputEditText.setOnClickListener {
            profileEditViewModel.cityClicked()
        }

        binding.saveChangesButton.setOnClick(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.saveChangesButton.showAnimation(true)
                profileEditViewModel.saveChanges {
                    binding.saveChangesButton.showAnimation(false)
                }
            }
        })
    }

    private fun setupObservers() {
        profileEditViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            if (isDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isDialogShown = false
            } else {
                this.findNavController().popBackStack()
            }
        })
        profileEditViewModel.profileLoaded.observe(viewLifecycleOwner, EventObserver {
            if (profileEditViewModel.getProfileEditForm().photo == null) {
                val initials = utils.stringUtils.getInitials(profileEditViewModel.getProfileEditForm().name)
                binding.profileInitialsViewElement.setInitials(initials)
                binding.photoImageView.visibility = View.GONE
                binding.profileInitialsViewElement.visibility = View.VISIBLE
            } else {
                binding.profileInitialsViewElement.visibility = View.GONE
                binding.photoImageView.visibility = View.VISIBLE
            }
        })
        profileEditViewModel.selectPhoto.observe(viewLifecycleOwner, EventObserver {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryResult.launch(pickIntent)
        })
        profileEditViewModel.city.observe(viewLifecycleOwner, EventObserver { codeValues ->
            this.isDialogShown = true
            this.dialogsManager?.let { manager ->
                val pickableCitiesAsCodeValues = profileEditViewModel
                    .getProfileEditForm()
                    .filteredPickableCities
                    .map { pc -> pc.toCodeValue() }

                manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                    title = getString(R.string.profile_edit_city),
                    codeValues = pickableCitiesAsCodeValues,
                    onSearchValueChanged = { value ->
                        this.profileEditViewModel.searchCity(value) {
                            val pickableCitiesAsCodeValues = profileEditViewModel
                                .getProfileEditForm()
                                .filteredPickableCities
                                .map { pc -> pc.toCodeValue() }
                            manager.setCodeValues(pickableCitiesAsCodeValues)
                        }
                    },
                    onCodeValueClicked = { code ->
                        val id = code.toIntOrNull()
                        if (id != null) {
                            this.profileEditViewModel.citySelected(id)
                            manager.hideBottomDialog()
                        }
                    },
                    onCancelClicked = {}
                ))
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogsManager = null
    }
}