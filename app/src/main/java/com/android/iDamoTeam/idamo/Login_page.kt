package com.android.iDamoTeam.idamo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_page.*
import kotlinx.android.synthetic.main.login_page.user_email
import kotlinx.android.synthetic.main.login_page.user_password

class Login_page : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var password = ""

    //progressDialog
    private lateinit var progressDialog: ProgressDialog

    private companion object {
        private const val TAG = "LoginActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        auth = Firebase.auth

        //progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCancelable(false)

        btnRegLogin.setOnClickListener {
            val intent = Intent(this, Register_page::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            Toast.makeText(applicationContext, "Register", Toast.LENGTH_SHORT).show()
        }

        btn_login.setOnClickListener {
            progressDialog.show()
            validateData()
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, Forgot_Password::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext, "Forgot Password", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Log.e("Who: ", currentUser.toString())
        if (currentUser == null) {
            Log.w(TAG, "User is not signed in. Please register to proceed!")
            return
        }else{
            val verification = auth.currentUser?.isEmailVerified
            if (verification == true) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "Please verify your Email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateData() {
        //get data
        email = user_email.text.toString().trim()
        password = user_password.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            user_email.error = "Invalid Email Format"
            user_email.requestFocus()
            progressDialog.dismiss()
        }else if (TextUtils.isEmpty(password)){
            //no password entered
            user_password.error = "Please Enter Password"
            user_password.requestFocus()
            progressDialog.dismiss()
        }else{
            //data is validated, begin login
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val verification = auth.currentUser?.isEmailVerified
                    Log.e("who: ", auth.currentUser?.email.toString())
                    if (verification == true) {
                        //login success
                        progressDialog.dismiss()
                        //get user info
                        val firebaseUser = auth.currentUser
                        val email = firebaseUser!!.email
                        Toast.makeText(this, "Logged In as $email", Toast.LENGTH_SHORT).show()
                        updateUI(firebaseUser)
                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this, "Please verify your Email.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //login Failed
                    progressDialog.dismiss()
                    Toast.makeText(this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }


//            .addOnSuccessListener { e ->
//                //login success
//                progressDialog.dismiss()
//                //get user info
//                val firebaseUser = auth.currentUser
//                val email = firebaseUser!!.email
//                Toast.makeText(this, "Logged In as $email", Toast.LENGTH_SHORT).show()
//                updateUI(firebaseUser)
//            }
//            .addOnFailureListener{ e ->
//                //login Failed
//                progressDialog.dismiss()
//                Toast.makeText(this, "Incorrect Email/Password", Toast.LENGTH_SHORT).show()
//                updateUI(null)
//            }
    }

}
