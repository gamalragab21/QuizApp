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
import com.developer.quizapp.databinding.ItemContainerProductBinding
import com.developer.quizapp.databinding.ItemSubjectLayoutBinding
import com.developer.quizapp.models.Subject
import com.developer.quizapp.utils.Constants.TAG
import javax.inject.Inject


class SubjectAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<SubjectAdapter.DepartmentViewHolder>() {




    //
    var departments: List<Subject>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Subject>() {
        override fun areContentsTheSame(
            oldItem: Subject,
            newItem: Subject,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        //
        override fun areItemsTheSame(
            oldItem: Subject,
            newItem: Subject,
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class DepartmentViewHolder(val itemBinding: ItemSubjectLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: Subject) {
          itemBinding.textUsername.text=item.name
            Log.i(TAG, "bindData: ${item.name}")
            setupActions(item)
        }

        private fun setupActions(item: Subject) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }

            itemBinding.acceptBtn.setOnClickListener {
                onItemAddExamClickListener?.let { click ->
                    click(item)
                }
            }
            itemBinding.closeImg.setOnClickListener {
                onCloseImageItemClicked?.let { click ->
                    click(item)
                }
            }
            itemBinding.userImage.setOnClickListener {
                onAddPdfItemClicked?.let { click ->
                    click(item)
                }
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val itemBinding =
            ItemSubjectLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private var onItemClickListener: ((Subject) -> Unit)? = null

    fun setOnItemClickListener(listener: (Subject) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemAddExamClickListener: ((Subject) -> Unit)? = null

    fun setOnItemAddExamClickListener(listener: (Subject) -> Unit) {
        onItemAddExamClickListener = listener
    }
    private var onCloseImageItemClicked: ((Subject) -> Unit)? = null

    fun setOnCloseImageItemClicked(listener: (Subject) -> Unit) {
        onCloseImageItemClicked = listener
    }
   private var onAddPdfItemClicked: ((Subject) -> Unit)? = null

    fun setonAddPdfItemClicked(listener: (Subject) -> Unit) {
        onAddPdfItemClicked = listener
    }


}