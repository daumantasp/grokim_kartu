package com.dauma.grokimkartu.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.viewmodels.main.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var galleryResult: ActivityResultLauncher<Intent>
//    private var cameraResult: ActivityResultLauncher<Intent>
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
                    }
                }
            }
        }
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
        return view
    }

    override fun onResume() {
        bindDefaultPhotoIfNeeded()
        super.onResume()
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
    }

    private fun bindDefaultPhotoIfNeeded() {
        if (profileViewModel.getProfileForm().photo == null) {
            binding.photoImageView.setImageResource(R.drawable.user)
        }
    }

    private fun setupScrollView() {
        binding.profileScrollView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.profileHeaderViewElement.showShadow(scrollY > 0.0)
        }
    }
}