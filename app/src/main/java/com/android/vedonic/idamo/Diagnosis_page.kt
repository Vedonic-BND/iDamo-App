package com.android.vedonic.idamo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.diagnosis_page.*

class Diagnosis_page: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis_page)

        val image = findViewById<ImageView>(R.id.image_taken)

        val intent = intent
        val imageURI = intent.getParcelableExtra<Uri>("plant_image")
        Log.e("URI", imageURI.toString())
        image.setImageURI(imageURI)

        done_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}