package com.developer.quizapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developer.quizapp.databinding.ItemContainerProductBinding
import com.developer.quizapp.databinding.ItemHomeLayoutBinding
import com.developer.quizapp.models.QuestionList
import com.developer.quizapp.models.Subject
import com.developer.quizapp.utils.Constants.TAG
import javax.inject.Inject


class HomeAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<HomeAdapter.DepartmentViewHolder>() {


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

    inner class DepartmentViewHolder(val itemBinding: ItemHomeLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: Subject) {

            itemBinding.quizName.text = item.name
            itemBinding.pdfView.isVisible=item.fileURL.isNotEmpty()
            setupActions(item)
        }

        private fun setupActions(item: Subject) {
            itemBinding.viewSupprotRoot.setOnClickListener {

                onItemClickListener?.let { click ->
                    click(item)
                }
            }
            itemBinding.pdfView.setOnClickListener {
                onPdfViewerItemClickListener?.let { click ->
                    click(item)
                }
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val itemBinding =
            ItemHomeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    private var onPdfViewerItemClickListener: ((Subject) -> Unit)? = null

    fun setOnPdfViewerItemClickListener(listener: (Subject) -> Unit) {
        onPdfViewerItemClickListener = listener
    }


}