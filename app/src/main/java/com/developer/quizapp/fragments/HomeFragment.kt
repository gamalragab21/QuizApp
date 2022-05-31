package com.developer.quizapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import com.developer.quizapp.R
import com.developer.quizapp.data.local.ComplexPreferences
import com.developer.quizapp.databinding.FragmentHomeBinding
import com.developer.quizapp.fragments.admin.Department
import com.developer.quizapp.models.Level
import com.developer.quizapp.models.QuestionList
import com.developer.quizapp.models.Subject
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.*
import com.developer.quizapp.utils.Constants.TAG
import com.developer.quizapp.utils.dialog.CustomDialogue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var complexPreferences: ComplexPreferences
    private lateinit var uiCommunicationListener: UICommunicationHelper
    private lateinit var profsRef: DatabaseReference
    private var userInfo: User? = null
    private lateinit var quizRef: DatabaseReference
    private var questionList = ArrayList<QuestionList>()
    private lateinit var levelsRef: DatabaseReference
    private var levelsList: ArrayList<Level> = ArrayList()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var homeAdapter: HomeAdapter
    private lateinit var subjectRef: DatabaseReference
    private var subjectsList: ArrayList<Subject> = ArrayList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profsRef = FirebaseDatabase.getInstance().getReference("ProfessorAccept")
        quizRef = FirebaseDatabase.getInstance().getReference("Quiz")
        subjectRef = FirebaseDatabase.getInstance().getReference("Subjects")
        levelsRef = FirebaseDatabase.getInstance().getReference("Levels")

        userInfo = complexPreferences.getObject(Constants.USER_OBJECT_LOCAL, User::class.java)
        loadingUserInfo()
        setupActions()
        setupHomeRecyclerView()
        loadAllLevels()
//        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                uiCommunicationListener.isLoading(true,true)
//                subjectsList.clear()
//                snapshot.children.forEach {
//                    Log.i(TAG, "onDataChange: ${it.key}")
//                    subjectRef.child(it.key!!)
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val question = snapshot.getValue(Subject::class.java)
//                                Log.i(TAG, "onDataChange: $question")
//                                if (question!=null) subjectsList.add(question)
//                                uiCommunicationListener.isLoading(loading = false,
//                                    mainActivity = true)
//                                homeAdapter.departments=subjectsList
//                                homeAdapter.notifyDataSetChanged()
//                            }
//                            override fun onCancelled(error: DatabaseError) {
//                               snackbar(error.message)
//                                uiCommunicationListener.isLoading(loading = false,
//                                    mainActivity = true)
//
//                            }
//
//                        })
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                snackbar(error.message)
//                uiCommunicationListener.isLoading(false,true)
//
//            }
//
//        })

        homeAdapter.setOnItemClickListener {
            Log.i(TAG, "onViewCreated: ${it.toString()}")
            if (userInfo!!.role == 0) {
                val bundle = bundleOf("topicSelected" to it.id)
                findNavController().navigateSafely(R.id.action_homeFragment_to_quizFragment, bundle)
            } else {
                snackbar("your are not student to start quiz!")
            }
        }
        homeAdapter.setOnPdfViewerItemClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.fileURL))
            startActivity(browserIntent)
        }
    }

    private fun loadAllLevels() {
        uiCommunicationListener.isLoading(loading = true, mainActivity = true)
        levelsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                levelsList.clear()
                snapshot.children.forEach {
                    Log.i(TAG, "onDataChange: ${it.toString()}")
                    val productList = ArrayList<Department>()
                    it.child("products")
                        .children.forEach {
                            val depatment = it.getValue(Department::class.java)
                            productList.add(depatment!!)
                        }
                    val level = it.getValue(Level::class.java)
                    level?.products = productList
                    Log.i(TAG, "onDataChange: ${level.toString()}")
                    if (level != null) levelsList.add(level)
                }
                    initDialogModelSpinner(requireContext(), levelsList.toTypedArray())
//                levelsAdapter.levels = levelsList.reversed()
//                levelsAdapter.notifyDataSetChanged()

                uiCommunicationListener.isLoading(loading = false, mainActivity = true)
                return
            }

            override fun onCancelled(error: DatabaseError) {
                snackbar(error.message)
                uiCommunicationListener.isLoading(loading = false, mainActivity = true)

            }

        })
    }

    private fun initDialogModelSpinner(
        context: Context,
        modelResponse: Array<Level>,
    ) {
        val modelName = ArrayList<String>()
        val lelvels = ArrayList<Level>()
        if (modelResponse.isNullOrEmpty()) {
            modelName.add(0, getString(R.string.select_level))
        } else {
            modelResponse.forEach { level ->
                level.products.forEach {
                    lelvels.add(level)
                    modelName.add("level: ${level.name} , product: ${it.name}")
                }
            }
        }

        val modelAdapter = ArrayAdapter(
            context,
            R.layout.layout_spinner_levels,
            modelName
        )
        modelAdapter.setDropDownViewResource(R.layout.my_drop_down_item)
        binding.spinnerLevelModel.adapter = modelAdapter
        binding.spinnerLevelModel.setSelection(0)
        binding.spinnerLevelModel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    Log.i(TAG, "onItemSelected: $position")
                    val selectedItem: String? = parent?.getItemAtPosition(position) as String
                    loadAllSubjectQuiz(lelvels[position])
                }
            }
        return
    }

    private fun loadAllSubjectQuiz(it: Level) {
        Log.i(TAG, "loadAllSubjectQuiz:start ${it.toString()}")
        subjectsList.clear()
        homeAdapter.departments = subjectsList
        homeAdapter.notifyDataSetChanged()
        if (it.products.isEmpty()) {
            homeAdapter.departments = emptyList()
            uiCommunicationListener.isLoading(loading = false, mainActivity = true)
        }
        uiCommunicationListener.isLoading(true, mainActivity = true)

        it.products.forEach { department ->
            Log.i(TAG, "loadAllSubjectQuiz: ${department.id}")
            subjectRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val subject = it.getValue(Subject::class.java)
                        Log.i(TAG, "onDataChange: ${subject.toString()}")
                        if (subject != null) {
                            if (subject.departmentId == department.id) {
                                subjectsList.add(subject)
                                Log.i(TAG, "onDataChange: ${subject.toString()}")
                            }
                        }
                    }
                    homeAdapter.departments = subjectsList
                    homeAdapter.notifyDataSetChanged()
                    uiCommunicationListener.isLoading(loading = false, true)
                }

                override fun onCancelled(error: DatabaseError) {
                    snackbar(error.message)
                    uiCommunicationListener.isLoading(loading = false, true)

                }

            })
            uiCommunicationListener.isLoading(loading = false, mainActivity = true)

        }

        uiCommunicationListener.isLoading(loading = false, mainActivity = true)

    }

    private fun setupHomeRecyclerView() = binding.subjectRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        adapter = homeAdapter
    }

    private fun setupActions() {

//        binding.javaLayout.setOnClickListener {
//            topicSelected = "java"
//            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
//            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded)
//
//        }
//        binding.phpLayout.setOnClickListener {
//            topicSelected = "php"
//            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
//            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded)
//        }
//        binding.htmlLayout.setOnClickListener {
//            topicSelected = "html"
//            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
//            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded)
//        }
//        binding.androidLayout.setOnClickListener {
//            topicSelected = "android"
//            binding.androidLayout.setBackgroundResource(R.drawable.bg_rounded_strok)
//            binding.htmlLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.phpLayout.setBackgroundResource(R.drawable.bg_rounded)
//            binding.javaLayout.setBackgroundResource(R.drawable.bg_rounded)
//        }
//        binding.btnSave.setOnClickListener {
//            if (topicSelected.isEmpty()) {
//                snackbar("Must select any topics")
//                return@setOnClickListener
//            }
//            val bundle = bundleOf("topicSelected" to topicSelected)
//            findNavController().navigateSafely(R.id.action_homeFragment_to_quizFragment, bundle)
//        }

        binding.icAddImg.setOnClickListener {
            userInfo?.let {
                if (it.role == 1) {
                    // admin
                    findNavController().navigateSafely(R.id.action_homeFragment_to_adminFragment)
                } else if (it.role == 2) {
                    // professor

                    profsRef.child(userInfo?.userId!!)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(User::class.java)
                                user?.let {
                                    if (it.ifProfIsAccept) {
                                        findNavController().navigateSafely(R.id.action_homeFragment_to_professorFragment)
                                    } else {
                                        snackbar("Please Waiting to accept you account fom admin")
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                snackbar(error.message)
                            }

                        })

                } else {
                    snackbar("this optional not avalible for you")
                }
//                    CustomDialogue.showHintDialogAddQuestionQuiz(
//                        requireActivity()
//                    ) { matrial, question, option1, option2, option3, option4, correctAnswer ->
//
//                        addQuestion(matrial,
//                            question,
//                            option1,
//                            option2,
//                            option3,
//                            option4,
//                            correctAnswer)
//
//                    }
            }
        }
        binding.icProfile.setOnClickListener {
            CustomDialogue.showDialogForLogout(requireContext()) {
                lifecycleScope.launchWhenStarted {
                    FirebaseAuth.getInstance().signOut()
                    complexPreferences.clearAllSettings()
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
        complexPreferences.getObject<User>(Constants.USER_OBJECT_LOCAL, User::class.java).also {
            it?.let {
                bindUserData(it)
            }
        }


    }

    private fun bindUserData(user: User) {
        glide.load(user.imageProfile).into(binding.icProfile)
        binding.welcomeMessageTv.text = getString(R.string.hello, user.username)
        binding.icAddImg.isVisible = user.role != 0
        binding.accountType.text = getString(R.string.your_are,
            if (user.role == 0) "student" else if (user.role == 1) "admin" else "Professor")
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