package com.example.taskmanager.views

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ItemUserBinding
import com.example.taskmanager.db.User

class UserAdapter(
    val listener: HandleUserClick,
    val userList: MutableList<User>
) : RecyclerView.Adapter<UserAdapter.userVH>() {


    interface HandleUserClick {
        fun onEditClick(user: User)
        fun onDeleteClick(user: User)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userVH {
        return userVH(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: userVH,
        position: Int
    ) {
        userList[position].let { user ->
            holder.binding.apply {
                tvTitle.text = " ${user.title}"
                tvDescription.text = " ${user.description}"
                tvDate.text = " ${user.date}"
                tvStatus.text = " ${user.isCompleted}"



                if (user.isCompleted) {
                    tvStatus.text = "Task Completed"
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                } else {
                    tvStatus.text = "Task Pending"
                    tvStatus.setTextColor(Color.parseColor("#F44336"))
                }

                btnEdit.setOnClickListener {
                    listener.onEditClick(user)
                }


                btnDelete.setOnClickListener {

                    listener.onDeleteClick(user)

                }

            }
        }
    }

    override fun getItemCount(): Int = userList.size





    class userVH(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
}