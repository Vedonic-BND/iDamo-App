package com.android.iDamoTeam.idamo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.register_page.*
import kotlinx.android.synthetic.main.register_page.user_email
import kotlinx.android.synthetic.main.register_page.user_name
import kotlinx.android.synthetic.main.register_page.user_password
import kotlin.collections.HashMap

class Register_page : AppCompatActivity() {

    //progressDialog
    private lateinit var progressDialog: ProgressDialog

    private lateinit var auth: FirebaseAuth

    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    //firestore
    private lateinit var firestoreDb: FirebaseFirestore

    private var email = ""
    private var password = ""
    private var name = ""
    private var bio = ""
    private var age = ""

    private companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        //firestore
        firestoreDb = FirebaseFirestore.getInstance()


        //progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating Account...")
        progressDialog.setCancelable(false)

        btnLogRegister.setOnClickListener{
            val intent = Intent(this, Login_page::class.java)
            val msg = intent.putExtra("name", user_name.text.toString())
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
        }

        btn_register.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
        //get data
        email = user_email.text.toString().trim()
        password = user_password.text.toString().trim()
        name = user_name.text.toString().trim()
        bio = user_bio_reg.text.toString().trim()
        age = user_age.text.toString().trim()

        //validate data
        if (TextUtils.isEmpty((name))) {
            //no name entered
            user_name.error = "Please Enter Your Name"
        }else if (TextUtils.isEmpty((bio))) {
            user_bio_reg.setText("Bio").toString()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            user_email.error = "Invalid Email Format"
        }else if (TextUtils.isEmpty(password)){
            //no password entered
            user_password.error = "Please Enter Password"
        }else if (password.length < 6) {
            //password is less than 6 characters
            user_password.error = "Your password must contain atleast 6 characters"
        }else if(TextUtils.isEmpty(age)){
            //No age entered
            user_age.error = "Please Enter Your Name"
        }else if(age.toInt() < 18){
            //Basta di pwede minor
            user_age.error = "Only 18 Yrs Old and Above can register"
        }else{
            //data is validated, begin login
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        //create account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressDialog.dismiss()
                    val firebaseUser = auth.currentUser
                    val e_mail = firebaseUser!!.email

                    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
                    val userMap = HashMap<String, Any>()
                    userMap["uid"] = currentUserID
                    userMap["name"] = name.toUpperCase()
                    userMap["email"] = email
                    userMap["bio"] = bio
                    userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/idamo-4ea09.appspot.com/o/Default%20Images%2Fuser_no-frame.png?alt=media&token=7a16341d-e597-465a-ab77-bbbdfa7ad480"
                    userMap["age"] = age.toString()

                    val userRef = firestoreDb.collection("profile").document(currentUserID)
                    userRef.set(userMap)

                    //databaseReference?.child(currentUserID)!!.setValue(userMap)


                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Please verify your Email!", Toast.LENGTH_SHORT).show()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }

                    Toast.makeText(this, "Account created with email $e_mail", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, Login_page::class.java))
                    finish()

                } else {

                    progressDialog.dismiss()
                    Toast.makeText(this, "Sign Up Failed: " + it.exception?.message, Toast.LENGTH_SHORT).show()
                }

            }

//            .addOnSuccessListener { e ->
//                progressDialog.dismiss()
//                val firebaseUser = auth.currentUser
//                val e_mail = firebaseUser!!.email
//
//                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
//                val userMap = HashMap<String, Any>()
//                userMap["uid"] = currentUserID
//                userMap["name"] = name.toUpperCase()
//                userMap["email"] = email
//                userMap["bio"] = bio
//                userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/idamo-4ea09.appspot.com/o/Default%20Images%2Fuser_no-frame.png?alt=media&token=7a16341d-e597-465a-ab77-bbbdfa7ad480"
//
//                val userRef = firestoreDb.collection("profile").document(currentUserID)
//                userRef.set(userMap)
//
//                //databaseReference?.child(currentUserID)!!.setValue(userMap)
//
//
//                auth.currentUser?.sendEmailVerification()
//                    ?.addOnSuccessListener {
//                        Toast.makeText(this, "Please verify your Email!", Toast.LENGTH_SHORT).show()
//                    }
//                    ?.addOnFailureListener {
//                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//                    }
//
//                Toast.makeText(this, "Account created with email $e_mail", Toast.LENGTH_SHORT)
//                    .show()
//                startActivity(Intent(this, Login_page::class.java))
//                finish()
//            }
//            .addOnFailureListener { e ->
//                progressDialog.dismiss()
//                Toast.makeText(this, "Sign Up Failed: " + e.message, Toast.LENGTH_SHORT).show()
//            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

}