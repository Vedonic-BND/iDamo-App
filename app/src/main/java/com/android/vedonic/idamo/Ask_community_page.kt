package com.android.vedonic.idamo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.ask_community.*
import kotlinx.android.synthetic.main.edit_user_details.*
import java.util.*
import kotlin.collections.HashMap

class Ask_community_page: AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri?= null
    private var storagePostPicRef: StorageReference?= null

    private val GALLERY_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ask_community)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        pickFromGallery()

        val image_post = findViewById<ImageView>(R.id.image_post)
        image_post.setOnClickListener {
            pickFromGallery()
        }

        val publish_post = findViewById<MaterialButton>(R.id.publish_post)
        publish_post.setOnClickListener { publishPost() }


    }

    private fun publishPost() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Ask Community")
        progressDialog.setMessage("Publishing your Post...")


        if (
            description_post.text.toString() == ""){
                Toast.makeText(this, "Please write the description.", Toast.LENGTH_SHORT).show()
            }
            else if (imageUri == null) {
                Toast.makeText(this, "Please select your image.", Toast.LENGTH_SHORT).show()
            }

            else {
                progressDialog.show()
                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

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

                        //firestore
                        val ref = FirebaseFirestore.getInstance().collection("Posts")
                        val postId = Date().time.toString()

                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["description"] = description_post.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl

                        //firestore
                        ref.document(postId).set(postMap)

                        Toast.makeText(this, "Post has been published successfully!", Toast.LENGTH_SHORT).show()

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
        Picasso.get().load(imageUri).into(image_post)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type =  "image/*"
        val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920,1080)
            .start(this@Ask_community_page)
    }
}