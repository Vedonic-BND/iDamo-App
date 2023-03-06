package com.android.iDamoTeam.idamo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.iDamoTeam.idamo.utils.ProfanityCheckerUtils
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*

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
        progressDialog.setTitle("Edit Comment")
        progressDialog.setMessage("Saving your changes...")
        progressDialog.setCancelable(false)

        val profileImage = findViewById<CircleImageView>(R.id.image_profile)
        val userName = findViewById<TextView>(R.id.user_name)
        val comment_edit = findViewById<EditText>(R.id.edit_comment)

        val publisherID = FirebaseAuth.getInstance().currentUser!!.uid

        publisherInfo(profileImage, userName, publisherID)
        getComment(comment_edit)

        val save_comment_btn = findViewById<MaterialButton>(R.id.save_comment)
        save_comment_btn.setOnClickListener {
            if (TextUtils.isEmpty(comment_edit.text.toString())){
                Toast.makeText(this, "No comment added.", Toast.LENGTH_SHORT).show()
            }else{
                progressDialog.show()

                val check = comment_edit?.text.toString()
                Log.e("check", check)
                val profanityList = ProfanityCheckerUtils.loadProfanityListFromFile(this, R.raw.filipino_profanity_words)
                ProfanityCheckerUtils.checkForProfanity(check, profanityList) { result ->
                    Log.e("result", result)
                    if (result == "true") {
                        runOnUiThread {
                            Toast.makeText(this, "Please refrain from using profanity words.", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                    } else {
                        Log.e("text", comment_edit?.text.toString())
                        runOnUiThread {
                            saveComment(comment_edit, publisherID)
                            comment_edit?.setText("")
                        }
                    }
                }
            }

        }

    }

    private fun saveComment(comment_edit: EditText, publisherID: String) {
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