package com.developer.quizapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.developer.quizapp.R
import com.developer.quizapp.data.local.dataStore.DataStoreManager
import com.developer.quizapp.databinding.FragmentHomeBinding
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.UICommunicationHelper
import com.developer.quizapp.utils.dialog.CustomDialogue
import com.developer.quizapp.utils.navigateSafely
import com.developer.quizapp.utils.navigateToSetupActivity
import com.developer.quizapp.utils.snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private var topicSelected = ""
    private val navController by lazy { findNavController() }

    @Inject
    lateinit var glide: RequestManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingUserInfo()
        setupActions()
    }

    private fun setupActions() {
        binding.javaLayout.setOnClickListener {
            topicSelected = "java"
            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded)

        }
        binding.phpLayout.setOnClickListener {
            topicSelected = "php"
            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded)
        }
        binding.htmlLayout.setOnClickListener {
            topicSelected = "html"
            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded)
        }
        binding.androidLayout.setOnClickListener {
            topicSelected = "android"
            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded)
            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded)
        }
        binding.btnSave.setOnClickListener {
            if (topicSelected.isEmpty()) {
                snackbar("Must select any topics")
                return@setOnClickListener
            }
            val bundle = bundleOf("topicSelected" to topicSelected)
            navController.navigateSafely(R.id.action_homeFragment_to_quizFragment, bundle)
        }

        binding.icAddImg.setOnClickListener {
            CustomDialogue.showHintDialogAddQuestionQuiz(
                requireActivity()
            ) { matrial, question, option1, option2, option3, option4, correctAnswer ->

                addQuestion(matrial, question, option1, option2, option3, option4, correctAnswer)

            }
        }
        binding.icProfile.setOnClickListener {
            CustomDialogue.showDialogForLogout(requireContext()) {
                lifecycleScope.launchWhenStarted {
                    FirebaseAuth.getInstance().signOut()
                    dataStoreManager.logOut()
                    snackbar("User successfully signed out")
                    navigateToSetupActivity(requireContext(), requireActivity())
                }
            }
        }

    }

    private fun addQuestion(
        matrial: String,
        question: String,
        option1: String,
        option2: String,
        option3: String,
        option4: String,
        correctAnswer: String,
    ) {
        val hasmap = HashMap<String, String>()
        hasmap.put("answer", correctAnswer)
        hasmap.put("option1", option1)
        hasmap.put("option2", option2)
        hasmap.put("option3", option3)
        hasmap.put("option4", option4)
        hasmap.put("question", question)
        FirebaseDatabase.getInstance()
            .getReference(matrial)
            .push()
            .setValue(hasmap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    snackbar("Success Add Question")
                } else {
                    snackbar(it?.exception?.localizedMessage ?: "Failed")
                }
            }

    }

    private fun loadingUserInfo() {
        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                bindUserData(it)
            }
        }
    }

    private fun bindUserData(user: User) {
        glide.load(user.imageProfile).into(binding.icProfile)
        binding.welcomeMessageTv.text = getString(R.string.hello, user.username)
        binding.icAddImg.isVisible = user.admin
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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
}