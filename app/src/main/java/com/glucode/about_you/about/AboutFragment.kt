package com.glucode.about_you.about

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.glucode.about_you.R
import com.glucode.about_you.about.views.QuestionCardView
import com.glucode.about_you.databinding.CustomProfileViewBinding
import com.glucode.about_you.databinding.FragmentAboutBinding
import com.glucode.about_you.engineers.models.Engineer
import com.glucode.about_you.mockdata.MockData

class AboutFragment: Fragment() {
    private lateinit var binding: FragmentAboutBinding
    private lateinit var profileBinding: CustomProfileViewBinding
    private var selectedEngineer: Engineer? = null
    private val REQUEST_IMAGE_PICK = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)

        profileBinding = CustomProfileViewBinding.bind(binding.root.findViewById(R.id.profile_view))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the engineer's name from arguments
        val engineerName = arguments?.getString("name") ?: return
        val engineer = MockData.engineers.firstOrNull { it.name == engineerName }

        // Populate profile view with engineer details
        engineer?.let {
            profileBinding.name .text = it.name
            profileBinding.role.text = it.role
            // Set profile picture if available (use default otherwise)
            if (it.defaultImageName.isNotEmpty()) {
                profileBinding.profileImage.setImageURI(Uri.parse(it.defaultImageName))
            }
            profileBinding.profileImage.setOnClickListener {
                openGallery()
            }
        }
        loadProfileImage()
        setUpQuestions()
    }

    // a function for the saving the chosen profile using sharedPreferences
    private fun loadProfileImage() {
        val sharedPreferences = requireContext().getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        val savedImageUri = sharedPreferences.getString("profile_image_uri", "")
        if (!savedImageUri.isNullOrEmpty()) {
            profileBinding.profileImage.setImageURI(Uri.parse(savedImageUri))
        }
    }

    //
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // Handle image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                profileBinding.profileImage.setImageURI(it)
                savedProfileImageUri(it.toString())
                selectedEngineer?.defaultImageName = it.toString()

                // Notify EngineersFragment to update list
                updateEngineerImage(selectedEngineer?.name ?: "", it)
            }
        }
    }
        //implemented sharedPreferences to save the selected image for profile display
        private fun savedProfileImageUri(imageUri: String) {
            val sharedPreferences = requireContext().getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("profile_image_uri", imageUri).apply()

    }

    private fun updateEngineerImage(engineerName: String, newImageUri: Uri) {
        // Notify the EngineersFragment (or any fragment) of the profile image update
        parentFragmentManager.setFragmentResult(
            "profileImageUpdated",
            Bundle().apply {
                putString("engineerName", engineerName)
                putString("newImageUri", newImageUri.toString())
            }
        )
    }

    private fun setUpQuestions() {
        val engineerName = arguments?.getString("name")
        val engineer = MockData.engineers.first { it.name == engineerName }

        engineer.questions.forEach { question ->
            val questionView = QuestionCardView(requireContext())
            questionView.title = question.questionText
            questionView.answers = question.answerOptions
            questionView.selection = question.answer.index

            binding.container.addView(questionView)
        }
    }
}