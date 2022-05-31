package com.developer.quizapp.fragments.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developer.quizapp.activites.MainActivity
import com.developer.quizapp.R
import com.developer.quizapp.data.local.ComplexPreferences
import com.developer.quizapp.databinding.FragmentSplashBinding
import com.developer.quizapp.utils.Constants
import com.developer.quizapp.utils.deleteBackStakeAfterNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var complexPreferences: ComplexPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val anim = AnimationUtils.loadAnimation(context, R.anim.logo_anim)
        binding.splashLogo.startAnimation(anim)
        checkUserState()

    }

    private fun checkUserState() {
        Handler(Looper.getMainLooper()).postDelayed({
            complexPreferences.getBoolean(Constants.IS_LOGIN, false).let {
                lifecycleScope.launchWhenResumed {
                    if (!it) {
                        navigateToLoginFragment()
                    } else {
                        navigateToMainActivity()
                    }
                }
            }
        }, 2000)

    }


    private fun navigateToLoginFragment() {
        val options = deleteBackStakeAfterNavigate(R.id.splashFragment)
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action, options)
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