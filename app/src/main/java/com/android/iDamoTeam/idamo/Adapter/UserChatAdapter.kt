package com.android.iDamoTeam.idamo.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.iDamoTeam.idamo.ChatActivity
import com.android.iDamoTeam.idamo.R
import com.android.iDamoTeam.idamo.databinding.UserMessageItemBinding
import com.android.iDamoTeam.idamo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        holder.setIsRecyclable(false)
        if (holder.javaClass == UserMessageHolder::class.java) {
//        holder.binding.visibility = View.VISIBLE
//        holder.userName.visibility = View.GONE
//        viewHolder.binding.mlinear.visibility = View.GONE
//        Picasso.get().load(user.image).placeholder(R.drawable.add_image_icon).into(holder.binding.)

            val viewHolder = holder as UserMessageHolder

            if (user.image.toString() == null){

            }else{
                Picasso.get().load(user.image).placeholder(R.drawable.profile)
                    .into(viewHolder.binding.imageProfile)
            }

            viewHolder.binding.userName.text = user.name

            FirebaseFirestore.getInstance().collection("profile").document(currentUser).collection("messaged").document(user.uid)
                .get().addOnSuccessListener {
                    Log.e("it: ", it["Messaged"].toString())
                    val status = it["Messaged"].toString()
                    if (status == "Sent" || status == "Seen") {
                        viewHolder.binding.activeIndicator.setBackgroundResource(R.color.white)
                    }else if (status == "notSeen" ) {
                        viewHolder.binding.activeIndicator.setBackgroundResource(R.color.light_green)
                    }

            }
//            viewHolder.binding.activeIndicator.setBackgroundResource(R.color.light_green)

        }



        holder.itemView.setOnClickListener {
            FirebaseFirestore.getInstance().collection("profile").document(currentUser).collection("messaged").document(user.uid)
                .update("Messaged", "Seen")
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