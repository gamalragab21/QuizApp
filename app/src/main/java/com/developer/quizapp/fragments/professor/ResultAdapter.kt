package com.developer.quizapp.fragments.professor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developer.quizapp.R
import com.developer.quizapp.databinding.ItemContainerProductBinding
import com.developer.quizapp.databinding.ItemResultBinding
import com.developer.quizapp.databinding.ItemSubjectLayoutBinding
import com.developer.quizapp.models.Result
import com.developer.quizapp.models.Subject
import com.developer.quizapp.utils.Constants.TAG
import javax.inject.Inject


class ResultAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<ResultAdapter.DepartmentViewHolder>() {


    //
    var departments: List<Result>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areContentsTheSame(
            oldItem: Result,
            newItem: Result,
        ): Boolean {
            return oldItem.resultId == newItem.resultId
        }

        //
        override fun areItemsTheSame(
            oldItem: Result,
            newItem: Result,
        ): Boolean {
            return oldItem.resultId == newItem.resultId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class DepartmentViewHolder(val itemBinding: ItemResultBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: Result) {

            itemBinding.timeQuizTv.text =
                context.getString(R.string.you_finsih_quiz_in_1_25, item.timeValue)
            itemBinding.questionsQuizTv.text =
                context.getString(R.string.quesstion_number, item.QuestionsSize.toString())
            itemBinding.correctQuizTv.text =
                context.getString(R.string.correct_answer, item.CorrectAnswer.toString())
            itemBinding.incorrectQuizTv.text =
                context.getString(R.string.incorrect_answer_1, item.InCorrectAnswer.toString())
            itemBinding.textUsername.text = item.username
            glide.load(item.imageProfile).into(itemBinding.userImg)
            setupActions(item)
        }

        private fun setupActions(item: Result) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val itemBinding =
            ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {

        val department = departments[position]
        holder.apply {
            bindData(department)
        }


    }


    override fun getItemCount(): Int = departments.size

    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }


}