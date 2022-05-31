package com.developer.quizapp.fragments.professor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developer.quizapp.R
import com.developer.quizapp.data.local.ComplexPreferences
import com.developer.quizapp.databinding.FragmentAdmainBinding
import com.developer.quizapp.databinding.FragmentPofessorBinding
import com.developer.quizapp.fragments.admin.LevelsAdapter
import com.developer.quizapp.models.Result
import com.developer.quizapp.models.Subject
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.Constants
import com.developer.quizapp.utils.Constants.TAG
import com.developer.quizapp.utils.UICommunicationHelper
import com.developer.quizapp.utils.dialog.CustomDialogue
import com.developer.quizapp.utils.snackbar
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class ProfessorFragment : Fragment() {
    private var _binding: FragmentPofessorBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var complexPreferences: ComplexPreferences
    private lateinit var profsRef: DatabaseReference
    private lateinit var quizRef: DatabaseReference
    private var subjectsList: ArrayList<Subject> = ArrayList()
    private var resultsList: ArrayList<Result> = ArrayList()
    private lateinit var resultRef: DatabaseReference
    private lateinit var subjectRef: DatabaseReference
    private val storage = Firebase.storage
    private lateinit var subjectSelected:Subject
    @Inject
    lateinit var subjectAdapter: SubjectAdapter

    @Inject
    lateinit var resultAdapter: ResultAdapter
    private lateinit var uiCommunicationListener: UICommunicationHelper
    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = complexPreferences.getObject(Constants.USER_OBJECT_LOCAL, User::class.java)!!
        profsRef = FirebaseDatabase.getInstance().getReference("ProfessorAccept")
        quizRef = FirebaseDatabase.getInstance().getReference("Quiz")
        resultRef = FirebaseDatabase.getInstance().getReference("Results")
        subjectRef = FirebaseDatabase.getInstance().getReference("Subjects")

        setupRecyclerSubject()
        loadAllAssignSubject()
        setupRecyclerResult()
        subjectAdapter.setOnItemAddExamClickListener {
            CustomDialogue.showHintDialogAddQuestionQuiz(requireActivity()) { quizName, timeCounteQuiz, question, option1, option2, option3, option4, correctAnswer ->
                uiCommunicationListener.isLoading(loading = true, mainActivity = true)
                val hasmap = HashMap<String, Any>()
                val key = quizRef.push().key!!
                hasmap.put("timer", timeCounteQuiz)
                hasmap.put("quizName", quizName)
                hasmap.put("answer", correctAnswer)
                hasmap.put("option1", option1)
                hasmap.put("option2", option2)
                hasmap.put("option3", option3)
                hasmap.put("option4", option4)
                hasmap.put("question", question)
                hasmap.put("profId", user.userId)
                hasmap.put("subject", it)
                hasmap.put("quizId", key)
                quizRef.child(it.id)
                    .child(key)
                    .setValue(hasmap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            snackbar("Success add your exam")
                        } else {
                            snackbar(it.exception?.localizedMessage ?: "")
                        }
                        uiCommunicationListener.isLoading(loading = false, mainActivity = true)

                    }
            }
        }
        subjectAdapter.setOnCloseImageItemClicked {
            quizRef.child(it.id).removeValue().addOnCompleteListener {
                if (it.isSuccessful) snackbar("Success Delete All Question of this quiz")
            }
        }
        subjectAdapter.setonAddPdfItemClicked {
            subjectSelected=it
            pickFromGallery()
        }
        binding.layoutPay.setOnClickListener {
            loadAllAssignSubject()
            setupPayAndCollectClick(true)
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        binding.layoutCollect.setOnClickListener {
            loadIngResult()
            setupPayAndCollectClick(false)
        }
    }
    private fun pickFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT;
        intent.type = "application/pdf"
        val mimeTypes = arrayOf("application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, 201)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                201 -> {
                    uiCommunicationListener.isLoading(true)
                    data?.data?.let { uri ->
                        lifecycleScope.launchWhenCreated {
                            val imageUploadResult =
                                storage.getReference(subjectSelected.id).putFile(uri).await()
                           val imageURL = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
                            subjectSelected.fileURL=imageURL
                            subjectRef.child(subjectSelected.id)
                                .setValue(subjectSelected)
                                .addOnCompleteListener {
                                    uiCommunicationListener.isLoading(false)
                                    snackbar("Success Uploaded")
                                }
                        }
                    }
                }

            }
        }
    }

    private fun loadIngResult() {
        resultsList.clear()

        subjectsList.forEach {
            resultRef.child(it.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val result = it.getValue(Result::class.java)
                            Log.i( TAG, "onDataChange: $result")
                            if (result!=null)resultsList.add(result)
                        }
                        resultAdapter.departments=resultsList
                        resultAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                       snackbar(error.message)
                    }

                })
            resultAdapter.departments=resultsList
            resultAdapter.notifyDataSetChanged()
        }

    }

    private fun setupPayAndCollectClick(pay: Boolean) {
        binding.subjectRecyclerView.isVisible = pay
        binding.resultRecyclerView.isVisible = !pay
//        binding.layoutEmptyView.emptyView.isVisible = false

        if (pay) {
            binding.tvPay.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.layoutPay.setBackgroundResource(R.drawable.background_select_items_home)
            binding.tvCollect.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            binding.layoutCollect.setBackgroundResource(R.drawable.background_items_home)
        } else {
            binding.tvCollect.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.layoutCollect.setBackgroundResource(R.drawable.background_select_items_home)

            binding.tvPay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            binding.layoutPay.setBackgroundResource(R.drawable.background_items_home)
        }

    }

    private fun loadAllAssignSubject() {
        profsRef.child(user.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    subjectsList.clear()
                    snapshot.child("subject")
                        .children.forEach {
                            val subject = it.getValue(Subject::class.java)
                            subjectsList.add(subject!!)
                        }
                    subjectAdapter.departments = subjectsList
                    subjectAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    snackbar(error.message)
                }

            })
    }

    private fun setupRecyclerSubject() = binding.subjectRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = subjectAdapter

    }
    private fun setupRecyclerResult() = binding.resultRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = resultAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPofessorBinding.inflate(inflater, container, false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        uiCommunicationListener.isLoading(loading = false, mainActivity = true)
        _binding = null
    }
}