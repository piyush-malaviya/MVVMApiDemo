package com.pcm.mvvmapidemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pcm.mvvmapidemo.data.User
import com.pcm.mvvmapidemo.databinding.UserItemLayoutBinding
import com.pcm.mvvmapidemo.extensions.loadImage
import com.pcm.mvvmapidemo.listener.OnItemClickListener
import java.util.*

class UserAdapter(listener: OnItemClickListener<User>? = null) :
    BaseFilterAdapter<User, UserAdapter.TodoViewHolder>() {

    init {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder {
        val itemBinding =
            UserItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.onBind(getListItem(position))
    }

    override fun filterObject(
        filteredList: ArrayList<User>,
        item: User,
        searchText: String
    ) {
    }

    inner class TodoViewHolder(private var itemBinding: UserItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root),
        View.OnClickListener {

        fun onBind(user: User?) {
            itemBinding.ivImage.loadImage(user?.avatar_url)
            itemBinding.tvName.text = user?.login
            itemBinding.tvUrl.text = user?.url
            itemBinding.tvUrl.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition >= 0) {
                onItemClickListener?.onItemClick(v, getListItem(adapterPosition), adapterPosition)
            }
        }

    }
}