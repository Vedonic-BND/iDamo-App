package com.android.vedonic.idamo.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.vedonic.idamo.ChatActivity
import com.android.vedonic.idamo.R
import com.android.vedonic.idamo.databinding.UserMessageItemBinding
import com.android.vedonic.idamo.model.User
import com.bumptech.glide.Glide.init
import com.squareup.picasso.Picasso

class UserChatAdapter(var mContext: Context,
                      mUser: List<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    lateinit var mUser: ArrayList<User>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_message_item, parent, false)
        return UserMessageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = mUser[position]

        holder.setIsRecyclable(false)
        if (holder.javaClass == UserMessageHolder::class.java) {
//        holder.binding.visibility = View.VISIBLE
//        holder.userName.visibility = View.GONE
//        viewHolder.binding.mlinear.visibility = View.GONE
//        Picasso.get().load(user.image).placeholder(R.drawable.add_image_icon).into(holder.binding.)

            val viewHolder = holder as UserMessageHolder

            Log.i("user.image", user.image)
            if (user.image.toString() == null){

            }else{
                Picasso.get().load(user.image).placeholder(R.drawable.profile)
                    .into(viewHolder.binding.imageProfile)
            }


            viewHolder.binding.userName.text = user.name
        }

        holder.itemView.setOnClickListener {

            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.uid)
            pref.putString("profileImage", user.image)
            pref.putString("userName", user.name)
            pref.apply()

            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("image", user.image)
            intent.putExtra("uid", user.uid)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = mUser.size


    inner class UserMessageHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var binding: UserMessageItemBinding = UserMessageItemBinding.bind(itemView)
    }

    init {
        this.mUser = mUser as ArrayList<User>
    }


}