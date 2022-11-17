package com.android.vedonic.idamo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.vedonic.idamo.Adapter.UserAdapter
import com.android.vedonic.idamo.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class SearchActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null

    private var searchQuery: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.searchRecyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        mUser = ArrayList()
        userAdapter = UserAdapter(this, mUser as ArrayList<User>, true)
        recyclerView?.adapter = userAdapter

        val searchQuery = findViewById<EditText>(R.id.search_query)
        searchQuery.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchQuery.text.toString() == ""){

                }else{
                    recyclerView?.visibility = View.VISIBLE

                    val replaceYouFrag = findViewById<FrameLayout>(R.id.replaceYouFrag)
                    replaceYouFrag?.visibility = View.GONE


                    retrieveUsers()
                    searchUser(s.toString().toUpperCase())
                }
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchUser(input: String) {
        //firestore
        val query = FirebaseFirestore.getInstance()
            .collection("profile")
            .orderBy("name")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addSnapshotListener { snapshot, exception ->
            val userList = snapshot!!.toObjects(User::class.java)
            mUser?.clear()

            mUser?.addAll(userList)
            userAdapter?.notifyDataSetChanged()

        }


        //firebase database
//        val query = FirebaseDatabase.getInstance().reference
//            .child("profile")
//            .orderByChild("name")
//            .startAt(input)
//            .endAt(input + "\uf8ff")
//
//        query.addValueEventListener(object: ValueEventListener{
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                mUser?.clear()
//
//                for (snapshot in dataSnapshot.children) {
//                    val user = snapshot.getValue(User::class.java)
//                    if (user != null){
//                        mUser?.add(user)
//                    }
//                }
//                userAdapter?.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun retrieveUsers() {
        //firestore
        val userRef = FirebaseFirestore.getInstance().collection("profile")

        userRef.addSnapshotListener { snapshot, exception ->
            val userList = snapshot!!.toObjects(User::class.java)
            if (searchQuery?.text.toString() == "") {
                mUser?.clear()
                mUser?.addAll(userList)
                userAdapter?.notifyDataSetChanged()
            }

        }


        //firebase database
//        val userRef = FirebaseDatabase.getInstance().reference.child("profile")
//        userRef.addValueEventListener(object: ValueEventListener{
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (searchQuery?.text.toString() == "") {
//                    mUser?.clear()
//
//                    for (snapshot in dataSnapshot.children) {
//                        val user = snapshot.getValue(User::class.java)
//                        if (user != null){
//                            mUser?.add(user)
//                        }
//                    }
//
//                    userAdapter?.notifyDataSetChanged()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
    }


}