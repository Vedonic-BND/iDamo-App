package com.android.vedonic.idamo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private var receiverName: String? = null
    private var receiverProfile: String? = null
    private var senderRoom: String? = null


    private lateinit var userName: String
    private lateinit var profileImage: String
    private lateinit var profileId: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMessagesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        firestoreDb = FirebaseFirestore.getInstance()

        mUser = ArrayList()


        val pref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
            this.profileImage = pref.getString("profileImage", "none").toString()
            this.userName = pref.getString("userName", "none").toString()
        }

        Log.i("ewan ko", "1st eto")
        userAdapter = UserChatAdapter(this, mUser as java.util.ArrayList<User>)

        Log.i("ewan ko 2", "2st eto")
        binding!!.messageRecycler.layoutManager = LinearLayoutManager(this@MyMessages)
        binding!!.messageRecycler.adapter = userAdapter
        Log.i("ewan ko 3", "3st eto")

        getPerson()

//        receiverUid = intent.getStringExtra("receiverUid")
//        receiverName = intent.getStringExtra("receiverName")
//        receiverProfile = intent.getStringExtra("receiverProfile")
//        Log.i("receiverUid", receiverUid.toString())
//        Log.i("receiverName", receiverName.toString())
//        Log.i("receiverProfile", receiverProfile.toString())


        senderRoom = firebaseUser!!.uid + receiverUid




        mUser?.clear()
        //myMessages()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPerson() {
        mUser?.clear()
        val currentId = FirebaseAuth.getInstance().uid
        val database = FirebaseFirestore.getInstance()
        FirebaseFirestore.getInstance().collection("profile").document(currentId.toString()).collection("messaged")
            .get().addOnSuccessListener { it ->
                val size = it.size()
                if (size.toString() != null) {
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
                        Log.d("documentID", i.toString())

                        database.collection("profile").document(i.toString())
                            .addSnapshotListener { snapshot, error ->
                                val userls = snapshot!!.toObject(User::class.java)
                                Log.d("userls", userls.toString())
                                mUser?.add(userls!!)
                                userAdapter?.notifyDataSetChanged()
                            }
                    }


                } else {

                    Toast.makeText(this, "Error loading messages!", Toast.LENGTH_SHORT).show()
                    binding!!.messageRecycler.visibility = View.INVISIBLE
                    binding!!.noMessages.visibility = View.VISIBLE
                    }




//                    database.collection("profile").document(currentId.toString()).collection("messaged")
//                        .document(i.toString()).addSnapshotListener {snapshot, exception ->
//                            val status = snapshot?.getString("Messaged")
//                            Log.i("ewan ko", i.toString())
//                            if (status.toString() == "Yes") {
//                                binding!!.messageRecycler.visibility = View.VISIBLE
//                            }else {
//                                binding!!.messageRecycler.visibility = View.INVISIBLE
//                            }
//                        }
//                }

                //set number of likes per post in array
//                var snapsize = 0
//                for ((count, i) in numberOfLikes.withIndex()) {
//                    FirebaseFirestore.getInstance().collection("Posts")
//                        .document(postIdArr[count].toString())
//                        .collection("Likes").whereEqualTo("liked", true).get()
//                        .addOnSuccessListener { snap ->
//                            snapsize = snap.size()
//                            Log.d(postIdArr[count].toString(), snapsize.toString())
//                            numberOfLikes[count] = snapsize.toString()
//                            total += snapsize
//                            Log.d("numberOfLikes[counter]", numberOfLikes[count].toString())
//                            Log.i("total", total.toString())
//                            leaflet_count.text = "$total"
//                        }
//                }
            }






        val currentID = FirebaseAuth.getInstance().uid


    }

//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun myMessages() {
//
//        val currentID = FirebaseAuth.getInstance().uid
//        val database = FirebaseFirestore.getInstance()
//        database.collection("profile").document(currentID.toString()).collection("messaged")
//            .addSnapshotListener {snapshot, exception ->
//                Log.i("snapshot", snapshot.toString())
//
//                val userls = snapshot!!.toObjects(User::class.java)
//                mUser?.clear()
//                mUser?.addAll(userls)
//                userAdapter?.notifyDataSetChanged()
//            }
//
//    }


}