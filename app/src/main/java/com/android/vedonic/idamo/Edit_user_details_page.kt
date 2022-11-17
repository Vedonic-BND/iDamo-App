package com.android.vedonic.idamo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Gallery
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.vedonic.idamo.fragments.YouFragment
import com.android.vedonic.idamo.model.User
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.edit_user_details.*

class Edit_user_details_page: AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri?= null
    private var storageProfilePicRef: StorageReference?= null

    //firestore
    private lateinit var firestoreDb: FirebaseFirestore

    private val GALLERY_REQUEST_CODE = 1234


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_user_details)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        //firestore
        firestoreDb = FirebaseFirestore.getInstance()

        userInfo()

        changeImage.setOnClickListener {
            checker = "clicked"
            pickFromGallery()
        }

        save_details.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndInfo()
            }else{
                updateUserInfoOnly()
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type =  "image/*"
        val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun uploadImageAndInfo() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Edit Account")
        progressDialog.setMessage("Updating your profile...")
        progressDialog.show()

        when {
            edit_profile_name.text.toString() == "" -> {
                Toast.makeText(this, "Please write your Name.", Toast.LENGTH_SHORT).show()
            }
            edit_profile_bio.text.toString() == "" -> {
                Toast.makeText(this, "Please write your Bio.", Toast.LENGTH_SHORT).show()
            }
            imageUri == null -> {
                Toast.makeText(this, "Please select your image.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let{
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri>{ task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        //firebase database
                        //val ref = FirebaseDatabase.getInstance().reference.child("profile")

                        //firestore
                        val ref = FirebaseFirestore.getInstance().collection("profile")


                        val userMap = HashMap<String, Any>()
                        userMap["name"] = edit_profile_name.text.toString().toUpperCase()
                        userMap["bio"] = edit_profile_bio.text.toString()
                        userMap["image"] = myUrl

                        ref.document(firebaseUser.uid).update(userMap)

                        //firebase database
                        //userRef.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account information has ben updated successfully!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }else{
                    Toast.makeText(this, "Error has occurred. Please try again.", Toast.LENGTH_SHORT).show()
                    Log.e("UploadImage:", "Image Selection Error")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK){
                    imageUri = result.uri
                    imageUri?.let {
                        setImageURI(it)
                    }
                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("UploadImage:", "Crop Error: ${result.error}")
                }
            }

        }

    }

    private fun setImageURI(imageUri: Uri) {
        Glide.with(this)
            .load(imageUri)
            .into(edit_user_picture)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this@Edit_user_details_page)
    }

    private fun updateUserInfoOnly() {
        when {
            edit_profile_name.text.toString() == "" -> {
                Toast.makeText(this, "Please write your Name.", Toast.LENGTH_SHORT).show()
            }
            edit_profile_bio.text.toString() == "" -> {
                Toast.makeText(this, "Please write your Bio.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //firebase database
                //val userRef = FirebaseDatabase.getInstance().reference.child("profile")

                //firestore
                val userRef = FirebaseFirestore.getInstance().collection("profile")

                val userMap = HashMap<String, Any>()
                userMap["name"] = edit_profile_name.text.toString().toUpperCase()
                userMap["bio"] = edit_profile_bio.text.toString()

                userRef.document(firebaseUser.uid).update(userMap)

                //firebase database
                //userRef.child(firebaseUser.uid).updateChildren(userMap)

                checker = ""

                Toast.makeText(this, "Account information has been updated successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
                finish()
            }
        }
    }

    private fun userInfo() {
        //firestore
        val userRef = firestoreDb.collection("profile").document(firebaseUser.uid)

        userRef.addSnapshotListener { snapshot, exception ->
            edit_profile_name.setText(snapshot?.getString("name"))
            edit_profile_bio.setText(snapshot?.getString("bio"))
            Picasso.get().load(snapshot?.getString("image")).placeholder(R.drawable.profile).into(edit_user_picture)
        }



//        val userRef = FirebaseDatabase.getInstance().reference.child("profile").child(firebaseUser.uid)
//
//        userRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                if (snapshot.exists()) {
//                    val user = snapshot.getValue<User>(User::class.java)
//
//                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(edit_user_picture)
//                    edit_profile_name.setText(user!!.getName())
//                    edit_profile_bio.setText(user!!.getBio())
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
    }
}