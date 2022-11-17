package com.android.vedonic.idamo.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.vedonic.idamo.Edit_user_details_page
import com.android.vedonic.idamo.R
import com.android.vedonic.idamo.fragments.YouFragment
import com.android.vedonic.idamo.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView




class UserAdapter (private var mContext: Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(com.android.vedonic.idamo.R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        val fragment = (mContext as FragmentActivity).supportFragmentManager.findFragmentByTag("UserTemp")
        val replaceYouFrag = (mContext as FragmentActivity).findViewById<FrameLayout>(R.id.replaceYouFrag)

        holder.userName.text = user.name
        if(holder.userImage == null) {
            // Load default image
            holder.userImage.setImageResource(com.android.vedonic.idamo.R.drawable.profile)
        }else{
            Picasso.get().load(user.image).into(holder.userImage)
        }

        if (fragment != null) {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.uid)
            pref.putString("profileImage", user.image)
            pref.putString("userName", user.name)
            pref.apply()


            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .add(R.id.replaceYouFrag, YouFragment(), "UserTemp")
                .commit()


            replaceYouFrag?.visibility = View.VISIBLE

            val recyclerView = (mContext as FragmentActivity).findViewById<RecyclerView>(R.id.searchRecyclerView)
            recyclerView?.visibility = View.GONE

        })
    }


    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(com.android.vedonic.idamo.R.id.user_name)
        var userImage: CircleImageView = itemView.findViewById(com.android.vedonic.idamo.R.id.user_picture)

    }
}