package com.developer.quizapp.fragments.admin

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
import com.developer.quizapp.utils.Constants.TAG


class DepartmentAdapter  constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder>() {




    //
    var departments: List<Department>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Department>() {
        override fun areContentsTheSame(
            oldItem: Department,
            newItem: Department,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        //
        override fun areItemsTheSame(
            oldItem: Department,
            newItem: Department,
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class DepartmentViewHolder(val itemBinding: ItemContainerProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: Department) {
          itemBinding.productName.text=item.name
            Log.i(TAG, "bindData: ${item.name}")
            setupActions(item)
        }

        private fun setupActions(item: Department) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }

            itemBinding.closeImg.setOnClickListener {
                onItemCancelClickListener?.let { click ->
                    click(item)
                }
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val itemBinding =
            ItemContainerProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private var onItemClickListener: ((Department) -> Unit)? = null

    fun setOnItemClickListener(listener: (Department) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemCancelClickListener: ((Department) -> Unit)? = null

    fun setOnItemCancelClickListener(listener: (Department) -> Unit) {
        onItemCancelClickListener = listener
    }


}