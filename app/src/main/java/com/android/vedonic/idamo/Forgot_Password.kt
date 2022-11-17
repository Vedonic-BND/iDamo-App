package com.android.vedonic.idamo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.user_email
import kotlinx.android.synthetic.main.login_page.*

class Forgot_Password : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var progressBar: ProgressBar? = null

    private companion object {
        private const val TAG = "ForgotPassActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = Firebase.auth

        progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar

        btn_reset.setOnClickListener {
            verify()
        }

    }

    private fun verify() {
        //get data
        email = user_email.text.toString().trim()

        if (email.isEmpty()) {
            user_email.error = "Email is Required"
            user_email.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            user_email.error = "Invalid Email Format"
            user_email.requestFocus()
        } else {
            resetPassword()
        }
    }

    private fun resetPassword() {
        progressBar?.visibility = View.VISIBLE
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { e ->
                //get user info
                val firebaseUser = auth.currentUser
                Toast.makeText(this, "Check your email to reset your password!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, Register_page::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            }
            .addOnFailureListener{ e ->
                Toast.makeText(this, "This user doesn't exist! ", Toast.LENGTH_LONG).show()
            }
    }
}