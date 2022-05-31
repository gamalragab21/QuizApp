package com.developer.quizapp.fragments.rgister

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.developer.quizapp.utils.Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS
import com.developer.quizapp.R
import com.developer.quizapp.activites.MainActivity
import com.developer.quizapp.data.local.ComplexPreferences
import com.developer.quizapp.databinding.FragmentSignupBinding
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.*
import com.google.firebase.database.FirebaseDatabase
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    AdapterView.OnItemSelectedListener {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val registerViewModel: RegisterViewModel by viewModels()

    @Inject
    lateinit var complexPreferences: ComplexPreferences
    private var imageUserProfile: String? = null
    private var roleUserSelected: Int = 0

    @Inject
    lateinit var glide: RequestManager

    private val navController by lazy { findNavController() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToRegisterFlow()
        imageUserProfile?.let {
            loadImage(it.toUri())
        }

        setupFragmentActions()

        initStatusSpinner()
    }

    private fun initStatusSpinner(
        statusResponse: Array<String> = resources.getStringArray(R.array.itemsRole),
    ) {

        val citiesAdapter = ArrayAdapter(
            requireContext(),
            R.layout.layout_spinner_categories,
            statusResponse
        )

        citiesAdapter.setDropDownViewResource(R.layout.my_drop_down_item)
        binding.layoutRoleSpinner.adapter = citiesAdapter
        binding.layoutRoleSpinner.setSelection(0)
        binding.layoutRoleSpinner.onItemSelectedListener = this

    }

    private fun subscribeToRegisterFlow() {
        lifecycleScope.launchWhenStarted {
            registerViewModel.registerStateRegister.collect {
                it.data?.let { user ->
                    uiCommunicationListener.isLoading(true, mainActivity = false)
                    if (user.role == 2) {
                        FirebaseDatabase.getInstance()
                            .getReference("ProfessorAccept")
                            .child(user.userId)
                            .setValue(user)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    saveDataInLocal(user)
                                } else {
                                    uiCommunicationListener.isLoading(loading = false,
                                        mainActivity = false)
                                    snackbar(it.exception?.localizedMessage ?: "")
                                }
                            }
                    } else {
                        saveDataInLocal(user)
                    }

                }
                uiCommunicationListener.isLoading(it.isLoading, false)
                it.error?.let {
                    snackbar(it)
                }
            }
        }
    }

    private fun saveDataInLocal(user: User) {

        complexPreferences.putObject(Constants.USER_OBJECT_LOCAL, user)
        complexPreferences.putBoolean(Constants.IS_LOGIN, true)
        complexPreferences.commit().also {
            if (it) navigateToMainActivity()
        }
        snackbar("Register Successfully")
        uiCommunicationListener.isLoading(loading = false,
            mainActivity = false)

    }

    private fun setupFragmentActions() {
        binding.icBackSignUp.setOnClickListener {
            navController.popBackStack()
        }
        binding.loginBtn.setOnClickListener {
            val name = binding.etNameRegister.text.toString()
            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPassLogin.text.toString()
            if (inputsIsVaild(name, email, password)) {
                registerViewModel.register(User(username = name, email = email,
                    role = roleUserSelected - 1
                ), password, imageUserProfile)
            }
        }
        binding.imgProfile.setOnClickListener {
            requestPermissions()
        }
        binding.openGallery.setOnClickListener {
            requestPermissions()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(
            Intent(requireContext(), MainActivity::class.java)
                .setFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK) and (Intent.FLAG_ACTIVITY_CLEAR_TOP))
        )
        requireActivity().finish()
    }

    private fun inputsIsVaild(
        firstName: String,
        email: String,
        password: String,
    ): Boolean {
        return if (roleUserSelected == 0) {
            snackbar("Please Select Your role*")
            binding.etNameRegister.requestFocus()
            false
        } else if (firstName.isEmpty()) {
            snackbar("Name is require*")
            binding.etNameRegister.requestFocus()
            false
        } else if (email.isEmpty()) {
            snackbar("Email is require*")
            binding.etEmailLogin.requestFocus()
            false
        } else if (!QuizValidation.isValidEmail(email)) {
            snackbar("Email is not valid")
            binding.etEmailLogin.requestFocus()
            false
        } else if (password.isEmpty()) {
            snackbar("Password is require*")
            binding.etPassLogin.requestFocus()
            false
        } else {
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiCommunicationListener.isLoading(loading = false, mainActivity = false)
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationHelper
        } catch (e: ClassCastException) {
            Log.e("AppDebug", "onAttach: $context must implement UICommunicationListener")
        }
    }

    private fun requestPermissions() {

        when {
            PermissionsUtility.hasReadExternalStoragePermissions(requireContext()) -> {
                openGallery()
                return
            }
            else -> when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.read_external_storage_message_permissions),
                        REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                else -> {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.read_external_storage_message_permissions),
                        REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS)
            EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this
            )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openGallery()
    }

    private fun openGallery() {
        CropImage.startPickImageActivity(requireContext(), this)
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).apply {
            setCropShape(CropImageView.CropShape.OVAL)
            setAspectRatio(1, 1)
            setMultiTouchEnabled(true)
            setGuidelines(CropImageView.Guidelines.ON)
        }.start(requireContext(), this)


    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = CropImage.getPickImageResultUri(requireContext(), data)
                    cropImage(uri)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    loadImage(result.uri)
                }
            }


        }

    }

    private fun loadImage(uri: Uri?) {
        uri?.let { myUri ->
            binding.openGallery.visibility = View.GONE
            imageUserProfile = myUri.toString()
            glide.load(myUri).into(binding.imgProfile)

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        roleUserSelected = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}