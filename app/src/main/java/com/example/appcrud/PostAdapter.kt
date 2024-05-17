package com.example.appcrud

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val postList: List<PostModelResponse>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_api_view, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.titleTextView.text = currentItem.title
        holder.bodyTextView.text = currentItem.body
    }

    override fun getItemCount() = postList.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val bodyTextView: TextView = itemView.findViewById(R.id.bodyTextView)
    }

}