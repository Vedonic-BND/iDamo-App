package com.android.iDamoTeam.idamo.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
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



    var mUser: ArrayList<User>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_message_item, parent, false)
        return UserMessageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = mUser[position]
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        holder.setIsRecyclable(false)
        if (holder.javaClass == UserMessageHolder::class.java) {

            val viewHolder = holder as UserMessageHolder

            if (user.image.toString() == null){
                Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile)
                    .into(viewHolder.binding.imageProfile)
            }else{
                Picasso.get().load(user.image).placeholder(R.drawable.profile)
                    .into(viewHolder.binding.imageProfile)
            }

            viewHolder.binding.userName.text = user.name

            //make BG of item white or light green.
            FirebaseFirestore.getInstance().collection("profile").document(currentUser).collection("messaged").document(user.uid)
                .get().addOnSuccessListener {
                    val status = it["Messaged"].toString()
                    if (status == "Sent" || status == "Seen") {
                        viewHolder.binding.activeIndicator.setBackgroundResource(R.color.white) //if seen or sent
                    }else if (status == "notSeen" ) {
                        viewHolder.binding.activeIndicator.setBackgroundResource(R.color.light_green) //if new or not seen
                    }
            }

            //if user is online or offline
            FirebaseFirestore.getInstance().collection("profile").document(user.uid).collection("presence").document(user.uid)
                .get().addOnSuccessListener {
                    val presence = it["Presence"].toString()
                    if (presence == "Online") {
                        viewHolder.binding.presence.setColorFilter(ContextCompat.getColor(mContext, R.color.online_dot)) //if user is Online
                    }else if (presence == "Offline" ) {
                        viewHolder.binding.presence.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_font))  //if user is Offline
                    }
            }
        }


        holder.itemView.setOnClickListener {
            FirebaseFirestore.getInstance().collection("profile").document(currentUser).collection("messaged").document(user.uid)
                .update("Messaged", "Seen")

            //broadcast user uid, image, and name to all activities that might use/get it
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