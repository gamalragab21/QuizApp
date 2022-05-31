package com.developer.quizapp.fragments.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.developer.quizapp.utils.Constants.TAG
import com.developer.quizapp.R
import com.developer.quizapp.activites.MainActivity
import com.developer.quizapp.data.local.ComplexPreferences
import com.developer.quizapp.databinding.FragmentQuizBinding
import com.developer.quizapp.models.QuestionList
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.Constants
import com.developer.quizapp.utils.UICommunicationHelper
import com.developer.quizapp.utils.dialog.CustomDialogue
import com.developer.quizapp.utils.snackbar
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var complexPreferences: ComplexPreferences
    private lateinit var uiCommunicationListener: UICommunicationHelper


    private var totalTimeInMinus = 0L
    private var seconds: Int = 0

    private val args: QuizFragmentArgs by navArgs()
    private var currentPosition: Int = 0

    private var selectedOptionByUser = ""
    private lateinit var subjectRef: DatabaseReference
    private lateinit var quizRef: DatabaseReference
    private lateinit var resultRef: DatabaseReference
    private lateinit var userInfo: User

    @Inject
    lateinit var glide: RequestManager

    private var questionsList = ArrayList<QuestionList>()
    private var cTimer: CountDownTimer? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userInfo = complexPreferences.getObject(Constants.USER_OBJECT_LOCAL, User::class.java)!!
        subjectRef = FirebaseDatabase.getInstance().getReference("Subjects")
        quizRef = FirebaseDatabase.getInstance().getReference("Quiz")
        resultRef = FirebaseDatabase.getInstance().getReference("Results")

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
            if (selectedOptionByUser.isEmpty()) {
                binding.layoutOption2.setBackgroundResource(R.drawable.btn_gradient_accent_cir)
                selectedOptionByUser = binding.tvOption2.text.toString()
                binding.tvOption2.setTextColor(Color.WHITE)
                revealAnswer()
                questionsList[currentPosition].userSelectedAnswer = selectedOptionByUser
            }
        }
        binding.layoutOption3.setOnClickListener {
            if (selectedOptionByUser.isEmpty()) {
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
        quizRef.child(topicSelected)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val item = it.getValue(QuestionList::class.java)
                        binding.topicSelectedTv.text = item?.quizName ?: ""

                        if (item != null) {
                            if (item.quizId.isNotEmpty()) questionsList.add(item)
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
            binding.noQuestionFound.visibility = VISIBLE
            binding.mainView.visibility = GONE
        } else {
            binding.noQuestionFound.visibility = GONE
            binding.mainView.visibility = VISIBLE
            startTimer(questionsList[0].timer)
            bindDataToView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindDataToView() {
        selectedOptionByUser = ""
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
        } else {
            cTimer?.cancel()
            CustomDialogue.showResultQuizDialog(requireActivity(),
                binding.timeValue.text.toString(),
                questionsList.size,
                getCorrectAnswer(),
                getInCorrectAnswer()) {
                addResult()

            }
        }
    }

    private fun addResult() {
        val resultId = resultRef.push().key!!
        val hasmap = HashMap<String, Any>()
        hasmap.put("CorrectAnswer", getCorrectAnswer())
        hasmap.put("InCorrectAnswer", getInCorrectAnswer())
        hasmap.put("QuestionsSize", questionsList.size)
        hasmap.put("resultId", resultId)
        hasmap.put("username", userInfo.username)
        hasmap.put("imageProfile", userInfo.imageProfile)
        hasmap.put("timeValue",
            (binding.timeValue.text.toString().toDouble() - questionsList[0].timer.toDouble()))
        resultRef.child(args.topicSelected).child(resultId)
            .setValue(hasmap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    navigateToMainActivity()
                } else {
                    snackbar(it.exception?.localizedMessage ?: "")
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
            cTimer?.cancel()
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun startTimer(time: String) {
        var mTimeLeftInMillis: Long = TimeUnit.MINUTES.toMillis(time.toLong())

        cTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            @SuppressLint("SetTextI18n", "ResourceAsColor")
            override fun onTick(millisUntilFinished: Long) {
                //  mTimeLeftInMillis = millisUntilFinished;
                val sDuraction = String.format(
                    Locale.ENGLISH,
                    "%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            )
                )

                binding.timeValue.text = "$sDuraction"


            }

            @SuppressLint("ResourceAsColor", "SetTextI18n")
            override fun onFinish() {
                snackbar("Time Over")
                CustomDialogue.showResultQuizDialog(requireActivity(),
                    binding.timeValue.text.toString(),
                    questionsList.size,
                    getCorrectAnswer(),
                    getInCorrectAnswer()) {
                    addResult()
                }
            }

        }.start()


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
        cTimer?.cancel()
    }
}