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
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.ui.BottomDialogData
import com.dauma.grokimkartu.viewmodels.main.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var galleryResult: ActivityResultLauncher<Intent>
//    private var cameraResult: ActivityResultLauncher<Intent>
    private var dialogsManager: DialogsManager? = null
    private var isProfileEditDialogShown: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    init {
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val width = resources.getDimensionPixelSize(R.dimen.default_profile_photo_width)
                    val height = resources.getDimensionPixelSize(R.dimen.default_profile_photo_height)
                    val image = utils.imageUtils.getImageWithAuthority(requireContext(), imageUri, width, height)
                    if (image != null) {
                        profileViewModel.getProfileForm().photo = image
                        profileViewModel.saveChanges()
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.model = profileViewModel
        val view = binding.root
        setupObservers()
        setupScrollView()
        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            profileViewModel.loadProfile()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isProfileEditDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isProfileEditDialogShown = false
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        profileViewModel.selectPhoto.observe(viewLifecycleOwner, EventObserver {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryResult.launch(pickIntent)
        })
        profileViewModel.profileLoaded.observe(viewLifecycleOwner, EventObserver {
            if (profileViewModel.getProfileForm().photo == null) {
                val initials = utils.stringUtils.getInitials(profileViewModel.getProfileForm().name)
                binding.profileInitialsViewElement.setInitials(initials)
                binding.photoImageView.visibility = View.GONE
                binding.profileInitialsViewElement.visibility = View.VISIBLE
            } else {
                binding.profileInitialsViewElement.visibility = View.GONE
                binding.photoImageView.visibility = View.VISIBLE
            }
        })
        // TODO: refactor
        profileViewModel.editInstrument.observe(viewLifecycleOwner, EventObserver {
            dialogsManager?.let { manager ->
                val dialogData = BottomDialogData(
                    title = getString(R.string.profile_instrument),
                    value = profileViewModel.getProfileForm().instrument,
                    valueLimit = profileViewModel.getProfileForm().instrumentLimit,
                    onSaveClicked = { value ->
                        manager.showBottomDialogLoading(true)
                        profileViewModel.getProfileForm().instrument = value
                        profileViewModel.saveChanges() {
                            manager.showBottomDialogLoading(false)
                            manager.hideBottomDialog()
                            utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                        }
                    },
                    onValueChanged = { value ->
                        val isSaveButtonEnabled = value != profileViewModel.getProfileForm().instrument
                        manager.enableBottomDialogSaveButton(isSaveButtonEnabled)
                    },
                    onCancelClicked = {
                        manager.hideBottomDialog()
                        utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                    }
                )
                manager.showBottomDialog(dialogData)
                isProfileEditDialogShown = true
            }
        })
        profileViewModel.editDescription.observe(viewLifecycleOwner, EventObserver {
            dialogsManager?.let { manager ->
                val dialogData = BottomDialogData(
                    title = getString(R.string.profile_description),
                    value = profileViewModel.getProfileForm().description,
                    valueLimit = profileViewModel.getProfileForm().descriptionLimit,
                    onSaveClicked = { value ->
                        manager.showBottomDialogLoading(true)
                        profileViewModel.getProfileForm().description = value
                        profileViewModel.saveChanges() {
                            manager.showBottomDialogLoading(false)
                            manager.hideBottomDialog()
                            utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                        }
                    },
                    onValueChanged = { value ->
                        val isSaveButtonEnabled = value != profileViewModel.getProfileForm().description
                        manager.enableBottomDialogSaveButton(isSaveButtonEnabled)
                    },
                    onCancelClicked = {
                        manager.hideBottomDialog()
                        utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                    }
                )
                manager.showBottomDialog(dialogData)
                isProfileEditDialogShown = true
            }
        })
        profileViewModel.editCity.observe(viewLifecycleOwner, EventObserver {
            dialogsManager?.let { manager ->
                val dialogData = BottomDialogData(
                    title = getString(R.string.profile_city),
                    value = profileViewModel.getProfileForm().city,
                    valueLimit = profileViewModel.getProfileForm().cityLimit,
                    onSaveClicked = { value ->
                        manager.showBottomDialogLoading(true)
                        profileViewModel.getProfileForm().city = value
                        profileViewModel.saveChanges() {
                            manager.showBottomDialogLoading(false)
                            manager.hideBottomDialog()
                            utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                        }
                    },
                    onValueChanged = { value ->
                        val isSaveButtonEnabled = value != profileViewModel.getProfileForm().city
                        manager.enableBottomDialogSaveButton(isSaveButtonEnabled)
                    },
                    onCancelClicked = {
                        manager.hideBottomDialog()
                        utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                    }
                )
                manager.showBottomDialog(dialogData)
                isProfileEditDialogShown = true
            }
        })
    }

    private fun setupScrollView() {
        binding.profileScrollView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.profileHeaderViewElement.showShadow(scrollY > 0.0)
        }
    }
}