package com.android.vedonic.idamo.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.vedonic.idamo.R
import com.android.vedonic.idamo.model.Post
import com.squareup.picasso.Picasso

class PhotoAdapter (private val mContext: Context,
                    private val mPost: List<Post>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder> () {



    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView
        var likes : TextView
        var description : TextView
        var comments : TextView

        init {
            postImage = itemView.findViewById(R.id.post_image)
            likes = itemView.findViewById(R.id.likes)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.numberOfComments)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {
        val post = mPost[position]

        if(holder.postImage == null) {
            // Load default image
            holder.postImage.setImageResource(R.drawable.add_image_icon)
        }else{
            Picasso.get().load(post.postimage).into(holder.postImage)
        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

}