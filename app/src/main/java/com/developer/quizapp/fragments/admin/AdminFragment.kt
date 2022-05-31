package com.developer.quizapp.fragments.admin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developer.quizapp.R
import com.developer.quizapp.data.local.ComplexPreferences
import com.developer.quizapp.databinding.FragmentAdmainBinding
import com.developer.quizapp.models.Level
import com.developer.quizapp.models.Subject
import com.developer.quizapp.models.User
import com.developer.quizapp.utils.Constants.TAG
import com.developer.quizapp.utils.UICommunicationHelper
import com.developer.quizapp.utils.dialog.CustomDialogue
import com.developer.quizapp.utils.snackbar
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AdminFragment : Fragment() {
    private var _binding: FragmentAdmainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var complexPreferences: ComplexPreferences

    @Inject
    lateinit var levelsAdapter: LevelsAdapter
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private var levelsList: ArrayList<Level> = ArrayList()
    private var profsList: ArrayList<User> = ArrayList()
    private var subjectsList: ArrayList<Subject> = ArrayList()
    private lateinit var levelsRef: DatabaseReference
    private lateinit var profsRef: DatabaseReference
    private lateinit var subjectRef: DatabaseReference
    private var assignButtonClicked: Boolean = false
    private lateinit var quizRef: DatabaseReference

    @Inject
    lateinit var profAdapter: ProfessorAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        levelsRef = FirebaseDatabase.getInstance().getReference("Levels")
        profsRef = FirebaseDatabase.getInstance().getReference("ProfessorAccept")
        subjectRef = FirebaseDatabase.getInstance().getReference("Subjects")
        quizRef = FirebaseDatabase.getInstance().getReference("Quiz")

        loadAllLevels()
        setupRecyclerViewProf()

        binding.layoutPay.setOnClickListener {
            loadAllLevels()
            setupPayAndCollectClick(true)
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        binding.layoutCollect.setOnClickListener {
            loadAllUnAcceptProfessor()
            setupPayAndCollectClick(false)
        }
        setupRecyclerViewAdding()

        binding.addLevelsTv.setOnClickListener {

            CustomDialogue.showDialogueForAddLevel(requireContext()) {
                uiCommunicationListener.isLoading(loading = true, mainActivity = true)

                val key: String = levelsRef.push().key!!
                levelsRef.child(key)
                    .setValue(
                        Level(key, it)
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            snackbar("Success Add Level")
                            uiCommunicationListener.isLoading(loading = false, mainActivity = true)

                        } else {
                            snackbar("Error ${it.exception}")
                            uiCommunicationListener.isLoading(loading = false, mainActivity = true)

                        }
                        return@addOnCompleteListener
                    }
            }
        }

        levelsAdapter.setOnItemArrowClickListener { department, imageView, recyclerView, linearLayout ->
            if (imageView.tag == "up") {
                imageView.setImageResource(R.drawable.ic_department_arrow_up)
                imageView.tag = "down"
                recyclerView.isVisible = true
                linearLayout.isVisible = true
            } else {
                imageView.setImageResource(R.drawable.ic_department_arrow_down)
                imageView.tag = "up"
                recyclerView.isVisible = false
                linearLayout.isVisible = false
            }
        }

        levelsAdapter.setOnItemAddDepartmentClickListener { level, i ->
            CustomDialogue.showDialogueForAddDepartment(requireContext()) {
                uiCommunicationListener.isLoading(loading = true, mainActivity = true)
                val key: String = levelsRef.push().key!!
                val department = Department(key, it)
                levelsRef.child(level.id)
                    .child("products")
                    .child(key).setValue(department)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            snackbar("Success Add Department")
                            uiCommunicationListener.isLoading(loading = false, mainActivity = true)
                        } else {
                            snackbar("Error ${it.exception}")
                            uiCommunicationListener.isLoading(loading = false, mainActivity = true)

                        }
                        loadAllLevels()
                        return@addOnCompleteListener
                    }
            }
        }

        levelsAdapter.setOnDepartmentItemClickListener { department, level, position ->

        }

        levelsAdapter.setOnItemCancelClickListener { department, level, position ->
            levelsRef.child(level.id)
                .child("products").child(department.id)
                .removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        snackbar("Delete Successfully")
                        subjectRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                subjectsList.clear()
                                snapshot.children.forEach {
                                    subjectsList.add(it.getValue(Subject::class.java)!!)
                                }
                                subjectsList.forEach {
                                    subjectRef.child(it.id).removeValue()
                                        .addOnCompleteListener { result ->
                                            if (result.isSuccessful) {
                                                quizRef.child(it.id).removeValue()
                                                    .addOnCompleteListener { result ->
                                                        profsRef.addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                snapshot.children.forEach { snap ->
                                                                    snap.child("subject").children.forEach { subject ->
                                                                        val item =
                                                                            subject.getValue(Subject::class.java)
                                                                        if (item != null) {
                                                                            if (item.id == it.id) profsRef.child(
                                                                                snap.key!!)
                                                                                .child("subject")
                                                                                .child(item!!.id)
                                                                                .removeValue()
                                                                                .addOnSuccessListener {

                                                                                }
                                                                        }
                                                                    }

                                                                }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                snackbar(error.message)
                                                            }

                                                        })

                                                    }

                                            } else snackbar(result.exception?.localizedMessage
                                                ?: "")
                                            return@addOnCompleteListener
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                snackbar(error.message)
                            }

                        })
                    } else snackbar(it.exception?.localizedMessage ?: "")
                    loadAllLevels()
                }

        }

        profAdapter.setOnItemClickListener {
            it.ifProfIsAccept = true
            profsRef.child(it.userId)
                .setValue(it).addOnCompleteListener {
                    if (it.isSuccessful) snackbar("Accept Successfully")
                    else snackbar(it.exception?.localizedMessage ?: "")
                }
        }
        profAdapter.setOnItemAssignButtonClickListener { user ->
            assignButtonClicked = true
            uiCommunicationListener.isLoading(loading = true, mainActivity = true)
            subjectRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (assignButtonClicked) {
                        subjectsList.clear()
                        snapshot.children.forEach {
                            val subject = it.getValue(Subject::class.java)
                            subjectsList.add(subject!!)
                        }
                        CustomDialogue.showAssignSubjectDialogue(requireContext(), subjectsList) {
                            profsRef.child(user.userId).child("subject")
                                .child(it.id).setValue(it).addOnCompleteListener {
                                    if (it.isSuccessful) snackbar("Success Assign this subject")
                                    else snackbar(it.exception?.localizedMessage ?: "")
                                }
                        }
                        uiCommunicationListener.isLoading(loading = false, mainActivity = true)
                        assignButtonClicked = false

                    }
                    return
                }

                override fun onCancelled(error: DatabaseError) {
                    snackbar(error.message)
                    uiCommunicationListener.isLoading(loading = false, mainActivity = true)
                    assignButtonClicked = false

                }

            })

        }
        levelsAdapter.setOnDepartmentItemClickListener { department, level, i ->
            val key: String = subjectRef.push().key!!

            CustomDialogue.showDialogueForAddSubejct(requireContext()) {

                subjectRef.child(key)
                    .setValue(Subject(key, department.id, it))
                    .addOnCompleteListener {
                        if (it.isSuccessful) snackbar("Success Ad Subject")
                        else snackbar(it.exception?.localizedMessage ?: "")
                    }
            }
        }
    }

    private fun loadAllUnAcceptProfessor() {
        profsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profsList.clear()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    profsList.add(user!!)
                }
                profAdapter.users = profsList
                profAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                snackbar(error.message)
            }

        })
    }

    private fun setupRecyclerViewProf() = binding.acceptRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = profAdapter
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
                levelsAdapter.levels = levelsList.reversed()
                levelsAdapter.notifyDataSetChanged()
                uiCommunicationListener.isLoading(loading = false, mainActivity = true)

            }

            override fun onCancelled(error: DatabaseError) {
                snackbar(error.message)
                uiCommunicationListener.isLoading(loading = false, mainActivity = true)

            }

        })
    }

    private fun setupPayAndCollectClick(pay: Boolean) {
        binding.acceptContentLayout.isVisible = !pay
        binding.addingContentLayout.isVisible = pay
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
            binding.imgPay.setImageDrawable(resources.getDrawable(R.drawable.ic_mange_levels))
            binding.imgCollect.setImageDrawable(resources.getDrawable(R.drawable.ic_accept))

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
            binding.imgPay.setImageDrawable(resources.getDrawable(R.drawable.ic_mange_levels_blue))
            binding.imgCollect.setImageDrawable(resources.getDrawable(R.drawable.ic_accept))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdmainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiCommunicationListener.isLoading(loading = false, mainActivity = true)
        _binding = null
    }

    private fun setupRecyclerViewAdding() = binding.levelsRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = levelsAdapter

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