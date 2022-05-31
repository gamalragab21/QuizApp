package com.developer.quizapp.utils.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.*
import com.developer.quizapp.utils.Constants.TAG
import com.developer.quizapp.R
import com.developer.quizapp.databinding.AddLevelLayoutDialogeBinding
import com.developer.quizapp.databinding.AssignSubjectLayoutBinding
import com.developer.quizapp.databinding.DialogAddQuestionBinding
import com.developer.quizapp.databinding.LogoutDialogLayoutBinding
import com.developer.quizapp.models.Subject
import com.developer.quizapp.utils.snackbar

object CustomDialogue {

    private var materialSelected: String = "Java"
    var dialog2: Dialog? = null

    @SuppressLint("InflateParams")
    fun showHintDialogCancelQuiz(context: Activity, onOkClicked: () -> Unit) {
        dialog2 = Dialog(context)
        dialog2!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_quiz, null)

        //val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        //titleTextView.text = title
        //val hintTextView = view.findViewById<TextView>(R.id.hintTextView)
        //hintTextView.text = hint

        val continueTextView = view.findViewById<TextView>(R.id.continueTextView)

        continueTextView.setOnClickListener {
            onOkClicked()
        }
        val closeTextView = view.findViewById<TextView>(R.id.closeTextView)

        closeTextView.text = context.getString(R.string.cancel)

        closeTextView.setOnClickListener {

            dialog2!!.dismiss()

        }

        dialog2!!.setContentView(view)
        dialog2!!.setCancelable(false)
        dialog2!!.setCanceledOnTouchOutside(false)
        try {
            if (!context.isFinishing) {
                if (!dialog2!!.isShowing)
                    dialog2!!.show()
            }
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
            //use a log message
        }

    }

    var dialog3: Dialog? = null

    @SuppressLint("StringFormatMatches")
    fun showResultQuizDialog(
        context: Activity,
        timeValue: String,
        size: Int,
        correctAnswer: Int,
        inCorrectAnswer: Int,
        okClicked: () -> Unit,
    ) {
        dialog3 = Dialog(context)
        dialog3!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog3!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog3!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_result_quiz, null)
        val timeValueTv = view.findViewById<TextView>(R.id.time_quiz_tv)
        val questionCount = view.findViewById<TextView>(R.id.questions_quiz_tv)
        val correct = view.findViewById<TextView>(R.id.correct_quiz_tv)
        val incorrect = view.findViewById<TextView>(R.id.incorrect_quiz_tv)

        timeValueTv.text = context.getString(R.string.you_finsih_quiz_in_1_25, timeValue)
        questionCount.text = context.getString(R.string.quesstion_number, size)
        correct.text = context.getString(R.string.correct_answer, correctAnswer)
        incorrect.text = context.getString(R.string.incorrect_answer_1, inCorrectAnswer)

        val continueTextView = view.findViewById<TextView>(R.id.continueTextView)

        continueTextView.setOnClickListener {
            okClicked()
        }

        dialog3!!.setContentView(view)
        dialog3!!.setCancelable(false)
        dialog3!!.setCanceledOnTouchOutside(false)
        try {
            if (!context.isFinishing) {
                if (!dialog3!!.isShowing)
                    dialog3!!.show()
            }
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
            //use a log message
        }
    }

    @SuppressLint("InflateParams")
    fun showHintDialogAddQuestionQuiz(
        context: Activity,
        onOkClicked: (
            String,String, String, String,
            String, String, String,
            String,
        ) -> Unit,
    ) {
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: DialogAddQuestionBinding =
            DialogAddQuestionBinding.inflate(LayoutInflater.from(context))

        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        //setSpinner(context, bind)
        dialog.show()
        bind.closeLayout.setOnClickListener {
            dialog.dismiss()
        }
        bind.addQuestionsNowBtn.setOnClickListener {
            val quizName = bind.etQuizNameText.text.toString()
            val timeCount = bind.etTimeText.text.toString()
            val question = bind.etQuestionText.text.toString()
            val answer1 = bind.etOption1Text.text.toString()
            val answer2 = bind.etOption2Text.text.toString()
            val answer3 = bind.etOption3Text.text.toString()
            val answer4 = bind.etOption4Text.text.toString()
            var correctAnswer = 0
            val selectedId: Int = bind.radioGroup.checkedRadioButtonId
            if (selectedId == R.id.radio_btn_1) correctAnswer = 1
            if (selectedId == R.id.radio_btn_2) correctAnswer = 2
            if (selectedId == R.id.radio_btn_3) correctAnswer = 3
            if (selectedId == R.id.radio_btn_4) correctAnswer = 4

            if (quizName.isEmpty()) {
                bind.root snackbar ("Must set The Quiz Name")
            }else if (timeCount.isEmpty()) {
                bind.root snackbar ("Must set The Time of quiz")
            } else if (question.isEmpty()) {
                bind.root snackbar ("Question Text is require")
            } else if (answer1.isEmpty()) {
                bind.root snackbar ("Answer One Text is require")
            } else if (answer2.isEmpty()) {
                bind.root snackbar ("Answer Second Text is require")
            } else if (answer3.isEmpty()) {
                bind.root snackbar ("Answer Three Text is require")
            } else if (answer4.isEmpty()) {
                bind.root snackbar ("Answer Four Text is require")
            } else if (correctAnswer < 0) {
                bind.root snackbar ("Must Select correct answer")
            } else {
                Log.i(TAG, "showHintDialogAddQuestionQuiz: $correctAnswer")
                onOkClicked(quizName,timeCount,
                    question,
                    answer1,
                    answer2,
                    answer3,
                    answer4,
                    when (correctAnswer) {
                        1 -> answer1
                        2 -> answer2
                        3 -> answer3
                        4 -> answer4
                        else -> answer1
                    })
                dialog.dismiss()
            }
        }


    }

//
//    private fun setSpinner(context: Activity, bind: DialogAddQuestionBinding) {
//        val citiesAdapter = ArrayAdapter(
//            context,
//            R.layout.layout_spinner_categories,
//            context.resources.getStringArray(R.array.matrial_items)
//        )
//
//        citiesAdapter.setDropDownViewResource(R.layout.my_drop_down_item)
//        bind.layoutMatrialSpinner.adapter = citiesAdapter
//        bind.layoutMatrialSpinner.setSelection(0)
//        bind.layoutMatrialSpinner.onItemSelectedListener = this
//    }

//    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        materialSelected = parent?.getItemAtPosition(position) as String
//    }
//
//    override fun onNothingSelected(p0: AdapterView<*>?) {
//
//    }

    fun showDialogForLogout(
        context: Context,
        logoutClickListener: (Boolean) -> Unit,
    ) {
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: LogoutDialogLayoutBinding =
            LogoutDialogLayoutBinding.inflate(LayoutInflater.from(context))

        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()

        bind.logoutBtnYes.setOnClickListener {
            // no button id = -1
            dialog.dismiss()
            logoutClickListener(true)
        }
        bind.logoutCancelBtn.setOnClickListener {
            // no button id = -1
            dialog.dismiss()
        }
    }

    fun showDialogueForAddLevel(
        context: Context,
        onItemAddClickListener: (String) -> Unit,
    ) {
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: AddLevelLayoutDialogeBinding =
            AddLevelLayoutDialogeBinding.inflate(LayoutInflater.from(context))

        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()


        bind.closeDialogue.setOnClickListener {
            dialog.dismiss()
        }
        bind.buttonAdd.setOnClickListener {
            val text = bind.edDepartmentName.text.toString()
            // no button id = -1
            dialog.dismiss()
            if (text.isEmpty()) {
                Toast.makeText(context, "Level name is empty", Toast.LENGTH_SHORT).show()
            } else onItemAddClickListener(text)
        }
    }

    fun showDialogueForAddDepartment(
        context: Context,
        onItemAddClickListener: (String) -> Unit,
    ) {
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: AddLevelLayoutDialogeBinding =
            AddLevelLayoutDialogeBinding.inflate(LayoutInflater.from(context))

        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()

        bind.tvDepartment.text = "Add Department"
        bind.inputDepartment.hint = "Department"
        bind.closeDialogue.setOnClickListener {
            dialog.dismiss()
        }
        bind.buttonAdd.setOnClickListener {
            val text = bind.edDepartmentName.text.toString()
            // no button id = -1
            dialog.dismiss()
            if (text.isEmpty()) {
                Toast.makeText(context, "Department name is empty", Toast.LENGTH_SHORT).show()
            } else onItemAddClickListener(text)
        }
    }

    fun showDialogueForAddSubejct(
        context: Context,
        onItemAddClickListener: (String) -> Unit,
    ) {
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: AddLevelLayoutDialogeBinding =
            AddLevelLayoutDialogeBinding.inflate(LayoutInflater.from(context))

        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()

        bind.tvDepartment.text = "Add Subject"
        bind.inputDepartment.hint = "Subject"
        bind.closeDialogue.setOnClickListener {
            dialog.dismiss()
        }
        bind.buttonAdd.setOnClickListener {
            val text = bind.edDepartmentName.text.toString()
            // no button id = -1
            dialog.dismiss()
            if (text.isEmpty()) {
                Toast.makeText(context, "Subject name is empty", Toast.LENGTH_SHORT).show()
            } else onItemAddClickListener(text)
        }
    }
    fun showAssignSubjectDialogue(context: Context,subjectResponse:ArrayList<Subject>, function: (Subject) -> Unit) {
        var subject:Subject?=null
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: AssignSubjectLayoutBinding =
            AssignSubjectLayoutBinding.inflate(LayoutInflater.from(context))
        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()
        initDialogBrandSpinner(
            context,
            bind.spinnerCarModel,
            brandResponses = subjectResponse.toTypedArray()
        ){
            subject=it
        }

        bind.btnAddCar.setOnClickListener {
            if (subject!=null){
                function(subject!!)
            }else{
                Toast.makeText(context, "Please, select subject", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

    }

    private fun initDialogBrandSpinner(
        context: Context,
        dialog: Spinner,
        brandResponses: Array<Subject>,
        function: (Subject) -> Unit
    ) {
        val brandName = ArrayList<String>()
        brandResponses.forEach {
            brandName.add(it.name)
        }
        val brandsAdapter = ArrayAdapter(
            context,
            R.layout.layout_spinner_cars,
            brandName
        )

        brandsAdapter.setDropDownViewResource(R.layout.my_drop_down_item)
        dialog.adapter = brandsAdapter
        dialog.setSelection(0)
        dialog.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem: String? = parent?.getItemAtPosition(position) as String
                    when (selectedItem) {
                        brandName[position] -> {
                            function(Subject(brandResponses[position].id,brandResponses[position].departmentId,brandResponses[position].name))
                        }
                    }
                }
            }
        return
    }
}