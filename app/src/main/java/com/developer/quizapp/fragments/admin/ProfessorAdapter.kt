package com.developer.quizapp.fragments.admin

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developer.quizapp.databinding.ItemUserSearchBinding
import com.developer.quizapp.models.User
import javax.inject.Inject


class ProfessorAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,

) : RecyclerView.Adapter<ProfessorAdapter.PayViewHolder>() {


    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }

        //
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PayViewHolder(val itemBinding: ItemUserSearchBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: User) {
            itemBinding.textEmail.text = item.email
            itemBinding.textUsername.text = "${item.username}"
            glide.load(item.imageProfile).into(itemBinding.userImage)
            itemBinding.acceptBtn.isVisible=!item.ifProfIsAccept
            itemBinding.assaignSubjectBtn.isVisible=item.ifProfIsAccept
            setupActions(item)
        }

        private fun setupActions(item: User) {

            itemBinding.acceptBtn.setOnClickListener {
                onItemClickListener?.let { action->
                    action(item)
                }
            }
            itemBinding.assaignSubjectBtn.setOnClickListener {
                onItemAssignButtonClickListener?.let { action->
                    action(item)
                }
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayViewHolder {
        val itemBinding =
            ItemUserSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PayViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: PayViewHolder, position: Int) {

        val user = users[position]


        holder.apply {
            bindData(user)
        }


    }


    override fun getItemCount(): Int = users.size

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }
    private var onItemAssignButtonClickListener: ((User) -> Unit)? = null

    fun setOnItemAssignButtonClickListener(listener: (User) -> Unit) {
        onItemAssignButtonClickListener = listener
    }
}