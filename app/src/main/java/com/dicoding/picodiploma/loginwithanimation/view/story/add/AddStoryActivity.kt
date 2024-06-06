package com.dicoding.picodiploma.loginwithanimation.view.story.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.StoryViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(false)
        lifecycleScope.launch {
            val repository = Injection.provideStoryRepository(this@AddStoryActivity)
            val factory = StoryViewModelFactory(repository)
            viewModel = ViewModelProvider(this@AddStoryActivity, factory)[AddStoryViewModel::class.java]

            if (!allPermissionsGranted()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }

            binding.buttonGallery.setOnClickListener { startGallery() }
            binding.buttonCamera.setOnClickListener { startCamera() }
            binding.buttonUpload.setOnClickListener { uploadImage() }

            viewModel.uploadResult.observe(this@AddStoryActivity) { result ->
                result.message?.let { showToast(it) }
                showLoading(false)
            }

            viewModel.isLoading.observe(this@AddStoryActivity) { isLoading ->
                showLoading(isLoading)
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.uploadCamera.setImageURI(it)
        }
        showLoading(false)
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }
    private fun uploadImage() {
        val description = binding.descriptionText.editText?.text.toString()

        if (description.isBlank()) {
            showToast(getString(R.string.empty_description_warning))
            return
        }
        if (currentImageUri == null) {
            showToast(getString(R.string.empty_image_warning))
            return
        }

        viewModel.uploadResult.observe(this@AddStoryActivity) { result ->
            Log.d("Upload", "Observing upload result")
            showLoading(false)
            if (result.error == false) {
                showToast("Upload's Done")
                Log.d("Upload", "Upload successful, navigating to StoryActivity")
                toStoryActivity() 
            } else {
                showToast(result.message ?: "Upload Failed")
                Log.e("Upload", "Upload failed with error: ${result.error}")
            }
        }

        showLoading(true)

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.uploadStory(multipartBody, requestBody)
        } ?: showToast(getString(R.string.empty_warning))
    }

    private fun toStoryActivity() {
        val intent = Intent(this, StoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}