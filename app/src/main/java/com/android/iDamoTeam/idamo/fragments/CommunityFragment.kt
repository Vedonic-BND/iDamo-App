package com.android.iDamoTeam.idamo.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.iDamoTeam.idamo.*
import com.android.iDamoTeam.idamo.Adapter.PostAdapter
import com.android.iDamoTeam.idamo.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_you.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var postAdapter: PostAdapter? = null
    private var mPost: MutableList<Post>? = null

    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_community, container, false)
        val toolbar = v.findViewById<androidx.appcompat.widget.Toolbar>(R.id.community_appbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val ask_community_btn = v.findViewById<Button>(R.id.ask_community_btn)
        ask_community_btn.setOnClickListener{
            val intent = Intent(activity, Ask_community_page::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
        }


        recyclerView = v.findViewById(R.id.communityRecyclerView)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager

        mPost = ArrayList()
        postAdapter = context?.let { PostAdapter(it, mPost as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        retrievePosts()
        return v
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun retrievePosts() {
        //firestore
        val postRef = FirebaseFirestore.getInstance().collection("Posts")

        postRef.addSnapshotListener { snapshot, exception ->
            val size = snapshot?.size()

            if ( size == 0 ) {
                noPost.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noPost.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            val postList = snapshot!!.toObjects(Post::class.java)
            mPost?.clear()
            mPost?.addAll(postList)
            postAdapter?.notifyDataSetChanged()
            }
        }


        //firebase database
//        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
//
//        postsRef.addValueEventListener(object : ValueEventListener {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(p0: DataSnapshot) {
//                postList?.clear()
//
//                for (snapshot in p0.children) {
//                    val post = snapshot.getValue(Post::class.java)
//
//                    if (post != null) {
//                        postList!!.add(post)
//                    }
//
//                    postAdapter!!.notifyDataSetChanged()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.community_appbar, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.search_bar){
            //do action here
            Toast.makeText(activity, "Search Here", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down)
        }

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

        return super.onOptionsItemSelected(item)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommunityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommunityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}