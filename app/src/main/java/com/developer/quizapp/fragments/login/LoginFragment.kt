package com.developer.quizapp.fragments.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developer.quizapp.activites.MainActivity
import com.developer.quizapp.R
import com.developer.quizapp.data.local.dataStore.DataStoreManager
import com.developer.quizapp.databinding.FragmentLoginBinding
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.UICommunicationHelper
import com.developer.quizapp.utils.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var  uiCommunicationListener: UICommunicationHelper
    private val loginViewModel:LoginViewModel by viewModels()

    private val navController by lazy { findNavController() }
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    @Inject
    lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActions()
        subscribeToLoginFlow()
    }

    private fun setupActions() {
        binding.loginBtn.setOnClickListener {
            val email=binding.etEmailLogin.text.toString().trim()
            val password=binding.etPassLogin.text.toString().trim()
            if (inputsValid(email,password)){
                loginViewModel.login(email,password)
            }
        }
        binding.tvSignUp.setOnClickListener {
            navController.navigate(R.id.signUpFragment)
        }
    }


    private fun inputsValid(email: String, password: String): Boolean {
        return if (email.isEmpty()) {
            binding.etEmailLogin.requestFocus()
            binding.etEmailLogin.error = "Email is require"
            snackbar("Email is require")
            false
        } else if (password.isEmpty()) {


            binding.etPassLogin.requestFocus()
            binding.etPassLogin.error = "Password is require"
            snackbar("Password is require")
            false
        }

        else {
            true
        }

    }

    private fun subscribeToLoginFlow() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect {
                it.data?.let {
                    snackbar("Login Successfully")
                    saveDataInLocal(it)
                }
                uiCommunicationListener.isLoading(it.isLoading,false)
                it.error?.let {
                    snackbar(it)
                }
            }
        }
    }

    private fun saveDataInLocal(user: User) {
        lifecycleScope.launchWhenCreated {
            async {
                dataStoreManager.saveUserProfile(user)
            }.await()
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(
            Intent(requireContext(), MainActivity::class.java)
                .setFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK) and (Intent.FLAG_ACTIVITY_CLEAR_TOP))
        )
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        uiCommunicationListener.isLoading(loading = false, mainActivity = false)
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationHelper
        } catch (e: ClassCastException) {
            Log.e("AppDebug", "onAttach: $context must implement UICommunicationListener")
        }
    }
}