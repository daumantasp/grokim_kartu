package com.dauma.grokimkartu.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileBinding
import com.dauma.grokimkartu.general.EventObserver
import com.dauma.grokimkartu.viewmodels.main.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var startForResult: ActivityResultLauncher<Intent>

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    init {
        // TODO: refactor
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data?.data != null) {
                    val imageUri = it.data?.data!!
                    val image = getImageWithAuthority(imageUri)
                    if (image != null) {
                        profileViewModel.photoSelected(image)
                        binding.photoImageView.setImageBitmap(image)
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
        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            profileViewModel.loadProfile()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        profileViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        })
        profileViewModel.selectPhoto.observe(viewLifecycleOwner, EventObserver {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startForResult.launch(pickIntent)
        })
        profileViewModel.selectedPhoto.observe(viewLifecycleOwner, {
            binding.photoImageView.setImageBitmap(it)
        })
    }

    // TODO: refactor
    private fun getImageWithAuthority(uri: Uri): Bitmap? {
        return decodeUriStreamToSize(uri, 250, 250, requireContext())
    }

    private fun decodeUriStreamToSize(uri: Uri,
                              width: Int, height: Int, context: Context
    ): Bitmap? {
        var inputStream: InputStream? = null
        try {
            val options: BitmapFactory.Options
            inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    options.inSampleSize = calculateInSampleSize(
                        options.outWidth, options.outHeight,
                        width, height)
                    options.inJustDecodeBounds = false
                    val bitmap = BitmapFactory.decodeStream(
                        inputStream, null, options)
                    inputStream.close()
                    return bitmap
                } }
            return null
        } catch (e: Exception) {
            return null
        } finally {
            inputStream?.close()
        }
    }

    private fun calculateInSampleSize(
        width: Int, height: Int,
        reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            } }
        return inSampleSize
    }
}