package com.example.quizapp.fragments.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.example.quizapp.R
import com.example.quizapp.activites.MainActivity
import com.example.quizapp.data.local.dataStore.DataStoreManager
import com.example.quizapp.databinding.FragmentQuizBinding
import com.example.quizapp.models.QuestionList
import com.example.quizapp.utils.UICommunicationHelper
import com.example.quizapp.utils.dialog.CustomDialogue
import com.example.quizapp.utils.snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val navController by lazy { findNavController() }

    private var totalTimeInMinus = 0L
    private var seconds: Int = 0

    private val args: QuizFragmentArgs by navArgs()
    private var currentPosition: Int = 0

    private var selectedOptionByUser = ""

    @Inject
    lateinit var glide: RequestManager

    private var questionsList = ArrayList<QuestionList>()
    private var quizTimer: Timer? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here

                    showCancelDialogue()
                    // if you want onBackPressed() to be called as normal afterwards
//                    if (isEnabled) {
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    }
                }
            })
        binding.icQuizBack.setOnClickListener {
            showCancelDialogue()
        }

        loadQuestions(args.topicSelected)

        setupActions()


    }

    private fun setupActions() {
        binding.layoutOption1.setOnClickListener {
            if (selectedOptionByUser.isEmpty()) {
                binding.layoutOption1.setBackgroundResource(R.drawable.btn_gradient_accent_cir)
                selectedOptionByUser = binding.tvOption1.text.toString()
                binding.tvOption1.setTextColor(Color.WHITE)
                revealAnswer()
                questionsList[currentPosition].userSelectedAnswer = selectedOptionByUser
            }
        }
        binding.layoutOption2.setOnClickListener {
            if ( selectedOptionByUser.isEmpty()) {
                binding.layoutOption2.setBackgroundResource(R.drawable.btn_gradient_accent_cir)
                selectedOptionByUser = binding.tvOption2.text.toString()
                binding.tvOption2.setTextColor(Color.WHITE)
                revealAnswer()
                questionsList[currentPosition].userSelectedAnswer = selectedOptionByUser
            }
        }
        binding.layoutOption3.setOnClickListener {
            if ( selectedOptionByUser.isEmpty()) {
                binding.layoutOption3.setBackgroundResource(R.drawable.btn_gradient_accent_cir)
                selectedOptionByUser = binding.tvOption3.text.toString()
                binding.tvOption3.setTextColor(Color.WHITE)
                revealAnswer()
                questionsList[currentPosition].userSelectedAnswer = selectedOptionByUser
            }
        }
        binding.layoutOption4.setOnClickListener {
            if (selectedOptionByUser.isEmpty()) {
                binding.layoutOption4.setBackgroundResource(R.drawable.btn_gradient_accent_cir)
                selectedOptionByUser = binding.tvOption4.text.toString()
                binding.tvOption4.setTextColor(Color.WHITE)
                revealAnswer()
                questionsList[currentPosition].userSelectedAnswer = selectedOptionByUser
            }
        }


        binding.btnSave.setOnClickListener {
            changeToNextQuestions()
        }

    }

    private fun revealAnswer() {

        val currentItem = questionsList[currentPosition]
        if (currentItem.answer == binding.tvOption1.text) {
            binding.layoutOption1.setBackgroundResource(R.drawable.btn_gradient_primary_cir)
            binding.tvOption1.setTextColor(Color.WHITE)
        } else if (currentItem.answer == binding.tvOption2.text) {
            binding.layoutOption2.setBackgroundResource(R.drawable.btn_gradient_primary_cir)
            binding.tvOption2.setTextColor(Color.WHITE)
        } else if (currentItem.answer == binding.tvOption3.text) {
            binding.layoutOption3.setBackgroundResource(R.drawable.btn_gradient_primary_cir)
            binding.tvOption3.setTextColor(Color.WHITE)
        } else if (currentItem.answer == binding.tvOption4.text) {
            binding.layoutOption4.setBackgroundResource(R.drawable.btn_gradient_primary_cir)
            binding.tvOption4.setTextColor(Color.WHITE)
        }

    }

    private fun loadQuestions(topicSelected: String) {
        uiCommunicationListener.isLoading(true)
        binding.topicSelectedTv.text = topicSelected
        FirebaseDatabase.getInstance().getReference(topicSelected)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val item = it.getValue(QuestionList::class.java)
                        item?.questionId=it.key?:""
                        if (item != null) {
                            questionsList.add(item)
                        }
                        Log.i(TAG, "onDataChange: ${item}")
                    }

                    setupData()
                    uiCommunicationListener.isLoading(false)

                }

                override fun onCancelled(error: DatabaseError) {
                    snackbar(error.message)
                    uiCommunicationListener.isLoading(false)
                }

            })

    }

    private fun setupData() {
        if (questionsList.isEmpty()) {
            binding.noQuestionFound.visibility=VISIBLE
            binding.mainView.visibility= GONE
        } else {
            binding.noQuestionFound.visibility=GONE
            binding.mainView.visibility= VISIBLE
            startTimer()
            bindDataToView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDataToView() {
        selectedOptionByUser=""
        if ((currentPosition + 1) == questionsList.size) {
            binding.btnSave.text = getString(R.string.submit_quiz)
        }
        if (currentPosition < questionsList.size) {
            changeBackgrounds()
            val currentItem = questionsList[currentPosition]
            binding.counterAnswers.text = "${currentPosition + 1}/${questionsList.size}"
            binding.questionTv.text = currentItem.question
            binding.tvOption1.text = currentItem.option1
            binding.tvOption2.text = currentItem.option2
            binding.tvOption3.text = currentItem.option3
            binding.tvOption4.text = currentItem.option4
        }else{
            quizTimer?.purge()
            quizTimer?.cancel()
           CustomDialogue.showResultQuizDialog(requireActivity(),binding.timeValue.text.toString(),questionsList.size,getCorrectAnswer(),getInCorrectAnswer()){
               navigateToMainActivity()
           }
        }
    }

    private fun changeBackgrounds() {
        binding.layoutOption1.setBackgroundResource(R.drawable.bg_rounded)
        binding.layoutOption2.setBackgroundResource(R.drawable.bg_rounded)
        binding.layoutOption3.setBackgroundResource(R.drawable.bg_rounded)
        binding.layoutOption4.setBackgroundResource(R.drawable.bg_rounded)
        binding.tvOption1.setTextColor(Color.BLUE)
        binding.tvOption2.setTextColor(Color.BLUE)
        binding.tvOption3.setTextColor(Color.BLUE)
        binding.tvOption4.setTextColor(Color.BLUE)
    }

    private fun changeToNextQuestions() {
        currentPosition++
        bindDataToView()
    }

    private fun showCancelDialogue() {
        CustomDialogue.showHintDialogCancelQuiz(requireActivity()) {
            quizTimer?.purge()
            quizTimer?.cancel()
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun startTimer() {
        quizTimer = Timer()
        quizTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                if (seconds == 0) {
                    totalTimeInMinus--
                    seconds = 59
                } else if (seconds == 0 && totalTimeInMinus == 0L) {

                    quizTimer?.purge()
                    quizTimer?.cancel()

                    snackbar("Time Over")
                } else {
                    seconds--
                }
                requireActivity().runOnUiThread {
                    var finalMinutes = totalTimeInMinus.toString()
                    var finalSeconds = seconds.toString()

                    if (finalMinutes.length == 1) {
                        finalMinutes = "0$finalMinutes"
                    }
                    if (finalSeconds.length == 1) {
                        finalSeconds = "0$finalSeconds"
                    }
                    binding.timeValue.text = "$finalMinutes:$finalSeconds"
                }

            }

        }, 1000, 1000)
    }


    private fun getCorrectAnswer(): Int {
        var correctAnswer = 0;
        questionsList.forEach {
            if (it.answer == it.userSelectedAnswer) {
                correctAnswer++
            }
        }
        return correctAnswer
    }

    private fun getInCorrectAnswer(): Int {
        var inCorrectAnswer = 0;
        questionsList.forEach {
            if (it.answer != it.userSelectedAnswer) {
                inCorrectAnswer++
            }
        }
        return inCorrectAnswer
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
        _binding = FragmentQuizBinding.inflate(inflater, container, false)

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

    override fun onDestroy() {
        super.onDestroy()
        quizTimer?.purge()
        quizTimer?.cancel()
    }
}