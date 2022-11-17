package com.android.vedonic.idamo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.vedonic.idamo.model.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.ask_community.*
import kotlinx.android.synthetic.main.comment_item.*
import kotlinx.android.synthetic.main.login_page.*
import org.w3c.dom.Text

class EditCommentActivity : AppCompatActivity() {

    private companion object {
        private const val TAG = "EditCommentActivity_Tag"
    }

    private var commentID = ""
    private var postID = ""


    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_comment)

        commentID = intent.getStringExtra("commentid")!!
        postID  = intent.getStringExtra("postID")!!

        //val comment = intent.getStringExtra("comment")!!

       // Log.e("comment", comment)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val profileImage = findViewById<CircleImageView>(R.id.image_profile)
        val userName = findViewById<TextView>(R.id.user_name)
        val comment_edit = findViewById<EditText>(R.id.edit_comment)

        val publisherID = FirebaseAuth.getInstance().currentUser!!.uid

        publisherInfo(profileImage, userName, publisherID)
        getComment(comment_edit)

        val save_comment_btn = findViewById<MaterialButton>(R.id.save_comment)
        save_comment_btn.setOnClickListener { savePost(comment_edit, publisherID) }

    }

    private fun savePost(comment_edit: EditText, publisherID: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Edit Comment")
        progressDialog.setMessage("Saving your changes...")
        progressDialog.show()

        val userRef = FirebaseFirestore.getInstance().collection("Posts").document(postID)
            .collection("Comments").document(commentID)

        val commentMap = HashMap<String, Any>()
        commentMap["comment"] = comment_edit.text.toString()

        userRef.update(commentMap)

        Toast.makeText(this, "Changes has been saved successfully!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra("postId", postID)
        intent.putExtra("authorId", publisherID)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
        finish()
        progressDialog.dismiss()
    }

    private fun getComment(comment_edit: EditText) {
        val userRef = FirebaseFirestore.getInstance().collection("Posts").document(postID)
            .collection("Comments").document(commentID)
        userRef.addSnapshotListener {snapshot, exception ->
            val comment = snapshot?.getString("comment")
            comment_edit.setText(comment)
        }
    }


    private fun publisherInfo(profileImage: CircleImageView, userName: TextView,publisherID: String) {
        val userRef = FirebaseFirestore.getInstance().collection("profile").document(publisherID)

        userRef.addSnapshotListener { snapshot, exception ->
            userName.text = snapshot?.getString("name")
            Picasso.get().load(snapshot?.getString("image")).into(profileImage)
        }
    }
}