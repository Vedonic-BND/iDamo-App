package com.android.vedonic.idamo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.vedonic.idamo.Adapter.CommentAdapter
import com.android.vedonic.idamo.Adapter.MessageAdapter
import com.android.vedonic.idamo.Adapter.UserAdapter
import com.android.vedonic.idamo.Adapter.UserChatAdapter
import com.android.vedonic.idamo.databinding.ActivityChatBinding
import com.android.vedonic.idamo.databinding.ActivityMyMessagesBinding
import com.android.vedonic.idamo.model.Comment
import com.android.vedonic.idamo.model.Message
import com.android.vedonic.idamo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_my_messages.*
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.android.synthetic.main.receive_msg_item.*

class MyMessages : AppCompatActivity() {

    var binding : ActivityMyMessagesBinding? = null

    var userAdapter : UserChatAdapter?=null
    var mUser: MutableList<User>? = null


    private var firebaseUser: FirebaseUser? = null

    private var firestoreDb: FirebaseFirestore? = null

    private var receiverUid: String? = null
    private var senderRoom: String? = null


    private lateinit var userName: String
    private lateinit var profileImage: String
    private lateinit var profileId: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMessagesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        setSupportActionBar(binding!!.messagesappbar)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        firestoreDb = FirebaseFirestore.getInstance()

        mUser = ArrayList()


        val pref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
            this.profileImage = pref.getString("profileImage", "none").toString()
            this.userName = pref.getString("userName", "none").toString()
        }

        userAdapter = UserChatAdapter(this, mUser as java.util.ArrayList<User>)

        binding!!.messageRecycler.layoutManager = LinearLayoutManager(this@MyMessages)
        binding!!.messageRecycler.adapter = userAdapter

        getPerson()


        senderRoom = firebaseUser!!.uid + receiverUid


        mUser?.clear()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.search_bar){
            //do action here
            Toast.makeText(applicationContext, "Search Here", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPerson() {
        mUser?.clear()
        val currentId = FirebaseAuth.getInstance().uid
        val database = FirebaseFirestore.getInstance()
        FirebaseFirestore.getInstance().collection("profile").document(currentId.toString()).collection("messaged")
            .get().addOnSuccessListener { it ->
                val size = it.size()
                if (size != 0) {
                    binding!!.messageRecycler.visibility = View.VISIBLE
                    binding!!.noMessages.visibility = View.INVISIBLE


                    Log.i("size", size.toString())
                    val userID = arrayOfNulls<String>(size)


                    //set postid to array
                    for ((counter, i) in userID.withIndex()) {
                        val snap = it.documents[counter]
                        userID[counter] = snap.reference.id
                    }


                    for ((counter,i) in userID.withIndex()) {

                        database.collection("profile").document(i.toString())
                            .addSnapshotListener { snapshot, error ->
                                val userList = snapshot!!.toObject(User::class.java)
                                mUser?.add(userList!!)
                                userAdapter?.notifyDataSetChanged()
                            }


                    }


                } else {

                    Toast.makeText(this, "Error loading messages!", Toast.LENGTH_SHORT).show()
                    binding!!.messageRecycler.visibility = View.INVISIBLE
                    binding!!.noMessages.visibility = View.VISIBLE
                }


            }
    }

}