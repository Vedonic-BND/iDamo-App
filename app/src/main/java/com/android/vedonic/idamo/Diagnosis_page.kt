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
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.vedonic.idamo.databinding.DiagnosisPageBinding
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.diagnosis_page.*


class Diagnosis_page: AppCompatActivity() {

    private lateinit var binding: DiagnosisPageBinding

    data class FragmentData(val title: String, val disName: String, val conf: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DiagnosisPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        try {
            ApplicationObject.startMediaManager(this.applicationContext)
        } catch (e: Exception) {
            Log.e("error cloudinary", e.toString())
        }


        val image = findViewById<ImageView>(R.id.image_taken)

        val intent = intent
        val imageURI = intent.getParcelableExtra<Uri>("plant_image")
        val diseaseName = intent.getStringExtra("disease_name")
        val confidence = intent.getStringExtra("confidence")
        Log.e("URI", imageURI.toString())
        Log.e("diseaseName", diseaseName.toString())
        Log.e("confidence", confidence.toString())

        if (imageURI != null) {
            uploadToCloudinary(imageURI)
            image.setImageURI(imageURI)
        }

        val fragmentDataList =
            if (diseaseName != "Healthy Leaf") {
                listOf<FragmentData>(
                    FragmentData("Description", "$diseaseName", "$confidence %"),
                    FragmentData("Symptoms", "$diseaseName", ""),
                    FragmentData("Solution", "$diseaseName", "")
                )
            } else {
                listOf<FragmentData>(
                    FragmentData("Description", "$diseaseName", "$confidence %"),
                    FragmentData("Solution", "$diseaseName", "")
                )
            }

        val tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = fragmentDataList[position].title
        }

        binding.viewPager.adapter = DiagnosisAdapter(this, fragmentDataList)
        tabLayoutMediator.attach()




        done_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    class DiagnosisAdapter(activity: AppCompatActivity,
                           private val fragmentDataList: List<FragmentData>
    ): FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            Log.e("fragmentDataList SIZE", fragmentDataList.size.toString())
            return fragmentDataList.size
        }

        override fun createFragment(position: Int): Fragment {
            if (fragmentDataList.size == 3) {
                return when(position) {
                    0 -> ViewPagerFragmentDescription().apply {
                        arguments = Bundle().apply {
                            putString("diseaseName", fragmentDataList[position].disName)
                            putString("confidence", fragmentDataList[position].conf)
                        }
                    }
                    1 -> ViewPagerFragmentSymptoms().apply {
                        arguments = Bundle().apply {
                            putString("diseaseName", fragmentDataList[position].disName)
                        }
                    }
                    2 -> ViewPagerFragmentSolution().apply {
                        arguments = Bundle().apply {
                            putString("diseaseName", fragmentDataList[position].disName)
                        }
                    }
                    else -> throw RuntimeException("Invalid position: $position")
                }
            }else{
                return when(position) {
                    0 -> ViewPagerFragmentDescription().apply {
                        arguments = Bundle().apply {
                            putString("diseaseName", fragmentDataList[position].disName)
                            putString("confidence", fragmentDataList[position].conf)
                        }
                    }
                    1 ->  ViewPagerFragmentSolution().apply {
                        arguments = Bundle().apply {
                            putString("diseaseName", fragmentDataList[position].disName)
                        }
                    }
                    else -> throw RuntimeException("Invalid position: $position")
                }
            }
        }

    }

    class ViewPagerFragmentDescription: Fragment(R.layout.fragment_description) {
        private val diseaseName: String by lazy {
            requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
        }

        private val confidence: String by lazy {
            requireArguments().getString("confidence") ?: "There's some kind of error. Please try again later."
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            when (diseaseName) {
                "Downy Mildew" -> {
                    view.findViewById<TextView>(R.id.diseaseName)
                        .setText(R.string.downyMildewDiseaseTitle)
                    view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.downyMildewDiseaseDescription)
                }
                "Black Spots/Leaf Scars" -> {
                    view.findViewById<TextView>(R.id.diseaseName)
                        .setText(R.string.scarsSpotsDiseaseTitle)
                    view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.scarsSpotsDiseaseDescription)
                }
                "Shot Hole" -> {
                    view.findViewById<TextView>(R.id.diseaseName)
                        .setText(R.string.shotHoleDiseaseTitle)
                    view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.shotHoleDiseaseDescription)
                }
                "Healthy Leaf" -> {
                    view.findViewById<TextView>(R.id.diseaseName).setText(R.string.healthyLeafTitle)
                    view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.healthyLeafDescription)
                }
            }
        }
    }

    class ViewPagerFragmentSymptoms: Fragment(R.layout.fragment_symptoms) {
        private val diseaseName: String by lazy {
            requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            when (diseaseName) {
                "Downy Mildew" -> {
                    view.findViewById<TextView>(R.id.symptoms).setText(R.string.downyMildewDiseaseSymptoms)
                }
                "Black Spots/Leaf Scars" -> {
                    view.findViewById<TextView>(R.id.symptoms).setText(R.string.scarsSpotsDiseaseSymptoms)
                }
                "Shot Hole" -> {
                    view.findViewById<TextView>(R.id.symptoms).setText(R.string.shotHoleDiseaseSymptoms)
                }
                "Healthy Leaf" -> {
                    view.findViewById<TextView>(R.id.symptoms).visibility = View.GONE
                    view.findViewById<TextView>(R.id.symptoms).visibility = View.GONE
                }
            }
        }
    }

    class ViewPagerFragmentSolution: Fragment(R.layout.fragment_solution) {
        private val diseaseName: String by lazy {
            requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            when (diseaseName) {
                "Downy Mildew" -> {
                    view.findViewById<TextView>(R.id.solution).setText(R.string.downyMildewDiseaseSolution)
                    view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                }
                "Black Spots/Leaf Scars" -> {
                    view.findViewById<TextView>(R.id.solution).setText(R.string.scarsSpotsDiseaseSolution)
                    view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                }
                "Shot Hole" -> {
                    view.findViewById<TextView>(R.id.solution).setText(R.string.shotHoleDiseaseSolution)
                    view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                }
                "Healthy Leaf" -> {
                    view.findViewById<TextView>(R.id.solution).setText(R.string.healthyLeafDiseaseSolution)
                    view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                }
            }
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