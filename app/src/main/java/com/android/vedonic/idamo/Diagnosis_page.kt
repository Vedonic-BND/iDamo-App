package com.android.vedonic.idamo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.android.synthetic.main.diagnosis_page.*


class Diagnosis_page: AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diagnosis_page)

        try {
            ApplicationObject.startMediaManager(this.applicationContext)
        } catch (e: Exception) {
            Log.e("error cloudinary", e.toString())
        }


        val image = findViewById<ImageView>(R.id.image_taken)

        val intent = intent
        val imageURI = intent.getParcelableExtra<Uri>("plant_image")
        val diseaseName = intent.getStringExtra("disease_name")
        Log.e("URI", imageURI.toString())
        Log.e("diseaseName", diseaseName.toString())

        if (imageURI != null) {
            uploadToCloudinary(imageURI)
            image.setImageURI(imageURI)
        }


        val diseaseNameText = findViewById<TextView>(R.id.diseaseName)
        val diseaseDescText = findViewById<TextView>(R.id.diseaseDesc)
        val symptomsText = findViewById<TextView>(R.id.symptomsTitle)
        val symptomsDescText = findViewById<TextView>(R.id.symptoms)
        val solutionText = findViewById<TextView>(R.id.solution)


        when (diseaseName) {
            "Downy Mildew" -> {
                diseaseNameText.setText(R.string.downyMildewDiseaseTitle)
                diseaseDescText.setText(R.string.downyMildewDiseaseDescription)
                symptomsDescText.setText(R.string.downyMildewDiseaseSymptoms)
                solutionText.setText(R.string.downyMildewDiseaseSolution)
                solutionText.movementMethod = LinkMovementMethod.getInstance()
            }
            "Black Spots/Leaf Scars" -> {
                diseaseNameText.setText(R.string.scarsSpotsDiseaseTitle)
                diseaseDescText.setText(R.string.scarsSpotsDiseaseDescription)
                symptomsDescText.setText(R.string.scarsSpotsDiseaseSymptoms)
                solutionText.setText(R.string.scarsSpotsDiseaseSolution)
                solutionText.movementMethod = LinkMovementMethod.getInstance()
            }
            "Shot Hole" -> {
                diseaseNameText.setText(R.string.shotHoleDiseaseTitle)
                diseaseDescText.setText(R.string.shotHoleDiseaseDescription)
                symptomsDescText.setText(R.string.shotHoleDiseaseSymptoms)
                solutionText.setText(R.string.shotHoleDiseaseSolution)
                solutionText.movementMethod = LinkMovementMethod.getInstance()
            }
            "Healthy Leaf" -> {
                diseaseNameText.setText(R.string.healthyLeafTitle)
                diseaseDescText.setText(R.string.healthyLeafDescription)
                symptomsText.visibility = View.GONE
                symptomsDescText.visibility = View.GONE
                solutionText.setText(R.string.healthyLeafDiseaseSolution)
                solutionText.movementMethod = LinkMovementMethod.getInstance()
            }
        }

        done_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadToCloudinary(filepath: Uri) {

        val inputStream = contentResolver.openInputStream(filepath)
        val byteArray = inputStream?.readBytes()
        MediaManager.get().upload(byteArray).unsigned("iDamo-upload").callback(object :
            UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {

            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {

                Toast.makeText(this@Diagnosis_page, "Task Not successful $error", Toast.LENGTH_SHORT).show()
            }

            override fun onStart(requestId: String?) {

            }
        }).dispatch()
    }

    object ApplicationObject{
        private var mediaManager: Any? = null

        fun startMediaManager(context: Context){
            if (mediaManager == null) {
                val config: HashMap<String, String> = HashMap()

                config["cloud_name"] = "idamo-app"
                config["api_key"] = "641623683328436"
                config["api_secret"] = "wuXJHdJE1iVNtO_CtliphUjlMUE"
                MediaManager.init(context, config)
            }
        }
    }
}