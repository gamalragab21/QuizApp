package com.developer.quizapp.fragments.admin

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developer.quizapp.R
import com.developer.quizapp.databinding.ItemLevelBinding
import com.developer.quizapp.models.Level
import javax.inject.Inject


class LevelsAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {


    //
    var levels: List<Level>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Level>() {
        override fun areContentsTheSame(
            oldItem: Level,
            newItem: Level,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        //
        override fun areItemsTheSame(
            oldItem: Level,
            newItem: Level,
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class LevelViewHolder(val itemBinding: ItemLevelBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: Level) {
            if (layoutPosition == 0) {
                itemBinding.departmentArrow.setImageResource(R.drawable.ic_department_arrow_up)
                itemBinding.departmentArrow.tag = "down"
                itemBinding.recyclerViewAddProduct.isVisible = true
                itemBinding.lnAddProduct.isVisible = true
            } else {
                itemBinding.departmentArrow.setImageResource(R.drawable.ic_department_arrow_down)
                itemBinding.departmentArrow.tag = "up"
                itemBinding.recyclerViewAddProduct.isVisible = false
                itemBinding.lnAddProduct.isVisible = false
            }
            itemBinding.departmentName.text = item.name
            val departmentAdapter: DepartmentAdapter = DepartmentAdapter(glide, context)

            setupRecyclerViewDepartment(itemBinding.recyclerViewAddProduct, departmentAdapter)

            item.products?.let {
                departmentAdapter.departments = it
            }
            departmentAdapter.setOnItemClickListener { department->
                onDepartmentItemClickListener?.let { click ->
                    click(department,item,layoutPosition)
                }
            }
            departmentAdapter.setOnItemCancelClickListener {department->
                onItemCancelClickListener?.let { click ->
                    click(department,item,layoutPosition)
                }
            }
            itemBinding.departmentArrow.setOnClickListener {
                onItemArrowClickListener?.let { click ->
                    click(item,
                        itemBinding.departmentArrow,
                        itemBinding.recyclerViewAddProduct,
                        itemBinding.lnAddProduct)
                }
            }
            itemBinding.lnAddProduct.setOnClickListener {
                onItemAddDepartmentClickListener?.let { click ->
                    click(item, layoutPosition)
                }
            }
            setupActions(item)
        }

        private fun setupActions(item: Level) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val itemBinding =
            ItemLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LevelViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {

        val level = levels[position]
        holder.apply {
            bindData(level)
        }


    }


    override fun getItemCount(): Int = levels.size

    private var onItemClickListener: ((Level) -> Unit)? = null

    fun setOnItemClickListener(listener: (Level) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemArrowClickListener: ((Level, ImageView, RecyclerView, LinearLayout) -> Unit)? =
        null

    fun setOnItemArrowClickListener(listener: (Level, ImageView, RecyclerView, LinearLayout) -> Unit) {
        onItemArrowClickListener = listener
    }

    private var onItemAddDepartmentClickListener: ((Level, Int) -> Unit)? =
        null

    fun setOnItemAddDepartmentClickListener(listener: (Level, Int) -> Unit) {
        onItemAddDepartmentClickListener = listener
    }

    private var onDepartmentItemClickListener: ((Department,Level,Int) -> Unit)? = null

    fun setOnDepartmentItemClickListener(listener: (Department,Level,Int) -> Unit) {
        onDepartmentItemClickListener = listener
    }

    private var onItemCancelClickListener: ((Department,Level,Int) -> Unit)? = null

    fun setOnItemCancelClickListener(listener: (Department,Level,Int) -> Unit) {
        onItemCancelClickListener = listener
    }
    private fun setupRecyclerViewDepartment(
        recyclerViewAddProduct: RecyclerView,
        departmentAdapter: DepartmentAdapter,
    ) {
        recyclerViewAddProduct.apply {
            itemAnimator = null
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = departmentAdapter
            setHasFixedSize(true)

        }
    }
}