package com.developer.quizapp.fragments.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developer.quizapp.activites.MainActivity
import com.developer.quizapp.R
import com.developer.quizapp.data.local.dataStore.DataStoreManager
import com.developer.quizapp.databinding.FragmentSplashBinding
import com.developer.quizapp.utils.deleteBackStakeAfterNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val anim = AnimationUtils.loadAnimation(context, R.anim.logo_anim)
        binding.splashLogo.startAnimation(anim)
        checkUserState()

    }

    private fun checkUserState() {
        lifecycleScope.launchWhenStarted {
            delay(2000)
            dataStoreManager.getUserProfile().collect{
                if (it.userId.isEmpty()){
                   navigateToLoginFragment()
                }else{
                    navigateToMainActivity()
                }
            }

        }
    }

    private fun navigateToLoginFragment() {
        val options = deleteBackStakeAfterNavigate(R.id.splashFragment)
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        navController.navigate(action, options)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}