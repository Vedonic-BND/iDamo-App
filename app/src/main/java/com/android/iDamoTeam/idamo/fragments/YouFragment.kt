package com.android.iDamoTeam.idamo.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.iDamoTeam.idamo.*
import com.android.iDamoTeam.idamo.Adapter.PostAdapter
import com.android.iDamoTeam.idamo.R
import com.android.iDamoTeam.idamo.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_you.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YouFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YouFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    //firestore
    private lateinit var firestoreDb: FirebaseFirestore

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var mypostList: MutableList<Post>

    private lateinit var userName: String
    private lateinit var profileImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setHasOptionsMenu(true)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        //firestore
        firestoreDb = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_you, container, false)
        val toolbar = v.findViewById<androidx.appcompat.widget.Toolbar>(R.id.you_appbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)


        val edit_btn = v.findViewById<Button>(R.id.edit_button)
        val create_btn = v.findViewById<Button>(R.id.create_btn)
        val leaflet_container = v.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.leaflet_container)
        val leaflet_counter = v.findViewById<TextView>(R.id.leaflet_count)
        val message_button = v.findViewById<Button>(R.id.message_button)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        recyclerView = v.findViewById(R.id.youRecyclerView)
        recyclerView.setHasFixedSize(true)
        val lLManager = LinearLayoutManager(context)
        lLManager.reverseLayout = true
        lLManager.stackFromEnd = true
        recyclerView.layoutManager = lLManager

        mypostList = ArrayList()
        postAdapter = PostAdapter(requireActivity(), mypostList as ArrayList<Post>)
        recyclerView.adapter = postAdapter

        edit_btn.setOnClickListener{
            val intent = Intent(context, Edit_user_details_page::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
        }

        create_btn.setOnClickListener{
            val intent = Intent(activity, Ask_community_page::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
        }

        leaflet_container.setOnClickListener {
            Toast.makeText(activity, "Leaflet: Likes on all of your posts.", Toast.LENGTH_LONG).show()
        }


        if (pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
            this.profileImage = pref.getString("profileImage", "none").toString()
            this.userName = pref.getString("userName", "none").toString()
        }

        if (profileId == firebaseUser.uid) {
            edit_btn.text = "Edit"
            message_button.setOnClickListener {
                val intent = Intent(activity, MyMessages::class.java)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
            }
        }else if (profileId != firebaseUser.uid) {
            edit_btn?.visibility = View.GONE
            create_btn?.visibility = View.GONE
            message_button.setOnClickListener {
                val intent = Intent(activity, ChatActivity::class.java)

                firestoreDb.collection("profile").document(profileId)
                    .addSnapshotListener { snapshot, exception ->
                        userName = snapshot?.getString("name").toString()
                        profileImage = snapshot?.getString("image").toString()
                    }

                intent.putExtra("name", userName)
                intent.putExtra("image", profileImage)
                intent.putExtra("uid", profileId)

                Log.i("name", userName)
                Log.i("image", profileImage)
                Log.i("uid", profileId)

                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
            }
        }


        totalNumberOfLikes()


        userInfo()
        myPhotos()


        return v
    }

    private fun totalNumberOfLikes() {
        val postRef = FirebaseFirestore.getInstance().collection("Posts")

        postRef.whereEqualTo("publisher", profileId).get().addOnSuccessListener { it ->
            val size = it.size()
            val postIdArr = arrayOfNulls<String>(size)
            val numberOfLikes = arrayOfNulls<String>(size)
            var total = 0


            //set postid to array
            for ((counter, i) in postIdArr.withIndex()) {
                val snap = it.documents[counter]
                postIdArr[counter] = snap.reference.id
            }

            for ((counter,i) in postIdArr.withIndex()) {
                Log.d("documentID", i.toString())
            }

            //set number of likes per post in array
            var snapsize = 0
            for ((count, i) in numberOfLikes.withIndex()) {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(postIdArr[count].toString())
                    .collection("Likes").whereEqualTo("liked", true).get()
                    .addOnSuccessListener { snap ->
                        snapsize = snap.size()
                        Log.d(postIdArr[count].toString(), snapsize.toString())
                        numberOfLikes[count] = snapsize.toString()
                        total += snapsize
                        Log.d("numberOfLikes[counter]", numberOfLikes[count].toString())
                        Log.i("total", total.toString())
                        leaflet_count.text = "$total"
                    }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun myPhotos() {
        val postRef = FirebaseFirestore.getInstance().collection("Posts")

        postRef.whereEqualTo("publisher", profileId).get().addOnSuccessListener {
            val size = it.size()
            if ( size == 0 ) {
                noPost.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noPost.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                val postList = it!!.toObjects(Post::class.java)
                mypostList.clear()
                mypostList.addAll(postList)
                postAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.kebab, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.kebab_settings){
            //do action here
            Toast.makeText(activity, "Settings Coming Soon", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.kebab_rate){
            //do action here
            Toast.makeText(activity, "Rate Coming Soon", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.kebab_share){
            //do action here
            Toast.makeText(activity, "Share Coming Soon", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.kebab_Tutorial) {
            //do action here
            Toast.makeText(activity, "Tutorial Coming Soon", Toast.LENGTH_SHORT).show()
        }

        //logout
        if (id == R.id.logout){
            val dialog = AlertDialog.Builder(activity)
                .setTitle("Logout")
                .setMessage("Are you sure to leave?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null)
                .show()

            val currentID = FirebaseAuth.getInstance().uid
            firestoreDb.collection("profile").document(currentID.toString()).collection("presence")
                .document(currentID.toString()).update("Presence", "Offline")

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                Log.i("Dashboard_Fragment: ", "Clicked on positive button!")
                // Logout the user
                Firebase.auth.signOut()
                Toast.makeText(activity, "Logged out", Toast.LENGTH_SHORT).show()
                val logoutIntent = Intent(activity, Login_page::class.java)
                logoutIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(logoutIntent)
                activity?.finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun userInfo() {
        //firestore
        val userRef = firestoreDb.collection("profile").document(profileId)

        userRef.addSnapshotListener { snapshot, exception ->
            view?.findViewById<TextView>(R.id.user_name)?.text = snapshot?.getString("name")
            view?.findViewById<TextView>(R.id.user_bio)?.text = snapshot?.getString("bio")
            view?.findViewById<TextView>(R.id.leaflet_count)?.text = snapshot?.getString("leaflet")
            Picasso.get().load(snapshot?.getString("image")).into(view?.findViewById<CircleImageView>(R.id.user_picture))
        }


        //firebase database
        //val userRef = FirebaseDatabase.getInstance().reference.child("profile").child(profileId)

//        userRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue<User>(User::class.java)
//                if (snapshot.exists()) {
//                    view?.findViewById<TextView>(R.id.user_name)?.text = user!!.getName()
//                    view?.findViewById<TextView>(R.id.user_bio)?.text = user!!.getBio()
//                    Picasso.get().load(user!!.getImage()).into(view?.findViewById<CircleImageView>(R.id.user_picture))
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment YouFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YouFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}