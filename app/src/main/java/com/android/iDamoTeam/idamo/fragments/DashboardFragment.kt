package com.android.iDamoTeam.idamo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import com.android.iDamoTeam.idamo.R
import java.io.File
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.iDamoTeam.idamo.Diagnosis_page
import com.android.iDamoTeam.idamo.LibraryActivity
import com.android.iDamoTeam.idamo.Login_page
import com.android.iDamoTeam.idamo.ml.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.send_msg_item.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    private val IMAGE_CAPTURE_CODE: Int = 1001
    private val IMAGE_GALLERY_CODE: Int = 1002
    private lateinit var auth: FirebaseAuth
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val heal_your_plants_fragment = hyp_container()
    private val weather_fragment = weather_container()

    val REQUEST_CODE = 1
    private lateinit var filePhoto: File
    var image_rui: Uri? = null
    var imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        putFragmentInView(heal_your_plants_fragment)
        putFragmentInView2(weather_fragment)

        setHasOptionsMenu(true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            openCamera(requestCode, resultCode, data)
        }else if(requestCode == 1002){
            openGallery(requestCode, resultCode, data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        val toolbar = v.findViewById<androidx.appcompat.widget.Toolbar>(R.id.dashboard_appbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val upload_btn = v.findViewById<Button>(R.id.upload_btn)
        upload_btn.setOnClickListener{

            if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )

                Toast.makeText(activity, "Try Again!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission has already been granted
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                try {
                    startActivityForResult(intent, IMAGE_GALLERY_CODE)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "Try Again!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val take_a_picture_btn = v.findViewById<Button>(R.id.take_a_picture_btn)
        take_a_picture_btn.setOnClickListener{

            if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )

                Toast.makeText(activity, "Try Again!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission has already been granted
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "Try Again!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return v
    }

    private fun openCamera (requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            var imageBitmap = data?.extras?.get("data") as Bitmap
            Log.e("imageBitmap: ", imageBitmap.toString())

            var dimension = Math.min(imageBitmap.width, imageBitmap.height)
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false)


            var image = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val resolver = requireActivity().contentResolver
            val date = System.currentTimeMillis()


            val path: String = MediaStore.Images.Media.insertImage(resolver, image,
                "IMG_$date", null)

            Log.e("path", path)
            val filePath = Uri.parse(path)



            classifyImage(imageBitmap, filePath)

//
//            Log.e("imageUri", imageUri.toString())
//            val passintent = Intent(activity, Diagnosis_page::class.java)
//            passintent.putExtra("plant_image", imageUri)
//            startActivity(passintent)


        }
    }

    private fun openGallery (requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null){
            val filePath: Uri = data.getData()!!
            var imageBitmap = BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(filePath))

            Log.e("imageBitmap:", imageBitmap.toString())

            var dimension = Math.min(imageBitmap.width, imageBitmap.height)
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false)
            classifyImage(imageBitmap, filePath)

//            Log.e("filePath", filePath.toString())
//            val intent = Intent (activity, Diagnosis_page::class.java)
//            intent.putExtra("plant_image", filePath)
//            activity?.startActivity(intent)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun classifyImage(imageBitmap: Bitmap, filePath: Uri) {
        val model = LeafClassification.newInstance(requireContext())

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        val inputFeature = disease(inputFeature0, imageBitmap)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        Log.e("ouputs", outputs.toString())

        //get the highest confidence amongst the prediction
        val confidences = outputFeature0.floatArray
        Log.e("confidences:", confidences.toString())
        var maxPos = 0
        var maxConfidence = 0.0f
        for(i in 0 until confidences.size) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }

        val classes = arrayOf("Anthurium", "Celosia", "Mayana", "Rose", "Sunflower")

        val resultText = classes[maxPos]
        Log.e("class:", maxPos.toString())
        Log.e("result text:", resultText)

        val confidence = confidences[maxPos] * 100
        Log.e("confidence:", confidence.toString())

        classifyDisease(imageBitmap, resultText, filePath)

        // Releases model resources if no longer used.
        model.close()
    }

    private fun classifyDisease(imageBitmap: Bitmap, plantName: String, filePath: Uri){
        when (plantName) {
            "Anthurium" -> {
                Log.e("PlantName: ", plantName)
                val model = AnthuriumDiseaseModel.newInstance(requireContext())

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                val inputFeature = disease(inputFeature0, imageBitmap)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                Log.e("ouputs", outputs.toString())

                //get the highest confidence amongst the prediction
                val confidences = outputFeature0.floatArray
                Log.e("confidences:", confidences.toString())
                var maxPos = 0
                var maxConfidence = 0.0f
                for(i in 0 until confidences.size) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                val classes = arrayOf("Downy Mildew", "Healthy Leaf", "Black Spots/Leaf Scars", "Shot Hole")

                val resultText = classes[maxPos]
                Log.e("class:", maxPos.toString())
                Log.e("result text:", resultText)

                val confidence = confidences[maxPos] * 100
                Log.e("confidence:", confidence.toString())

                Log.e("filePath", filePath.toString())
                val intent = Intent (activity, Diagnosis_page::class.java)
                intent.putExtra("plant_image", filePath)
                intent.putExtra("disease_name", resultText)
                intent.putExtra("confidence", confidence.toString())
                activity?.startActivity(intent)

                // Releases model resources if no longer used.
                model.close()

            }
            "Celosia" -> {
                Log.e("PlantName: ", plantName)
                val model = CelosiaDiseaseModel.newInstance(requireContext())

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                val inputFeature = disease(inputFeature0, imageBitmap)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                Log.e("ouputs", outputs.toString())

                //get the highest confidence amongst the prediction
                val confidences = outputFeature0.floatArray
                Log.e("confidences:", confidences.toString())
                var maxPos = 0
                var maxConfidence = 0.0f
                for(i in 0 until confidences.size) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                val classes = arrayOf("Downy Mildew", "Healthy Leaf", "Black Spots/Leaf Scars")

                val resultText = classes[maxPos]
                Log.e("class:", maxPos.toString())
                Log.e("result text:", resultText)

                val confidence = confidences[maxPos] * 100
                Log.e("confidence:", confidence.toString())

                Log.e("filePath", filePath.toString())
                val intent = Intent (activity, Diagnosis_page::class.java)
                intent.putExtra("plant_image", filePath)
                intent.putExtra("disease_name", resultText)
                intent.putExtra("confidence", confidence.toString())
                activity?.startActivity(intent)

                // Releases model resources if no longer used.
                model.close()

            }
            "Mayana" -> {
                Log.e("PlantName: ", plantName)
                val model = MayanaDiseaseModel.newInstance(requireContext())

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                val inputFeature = disease(inputFeature0, imageBitmap)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                Log.e("ouputs", outputs.toString())

                //get the highest confidence amongst the prediction
                val confidences = outputFeature0.floatArray
                Log.e("confidences:", confidences.toString())
                var maxPos = 0
                var maxConfidence = 0.0f
                for(i in 0 until confidences.size) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                val classes = arrayOf("Downy Mildew", "Healthy Leaf", "Black Spots/Leaf Scars", "Shot Hole")

                val resultText = classes[maxPos]
                Log.e("class:", maxPos.toString())
                Log.e("result text:", resultText)

                val confidence = confidences[maxPos] * 100
                Log.e("confidence:", confidence.toString())

                Log.e("filePath", filePath.toString())
                val intent = Intent (activity, Diagnosis_page::class.java)
                intent.putExtra("plant_image", filePath)
                intent.putExtra("disease_name", resultText)
                intent.putExtra("confidence", confidence.toString())
                activity?.startActivity(intent)

                // Releases model resources if no longer used.
                model.close()

            }
            "Rose" -> {
                Log.e("PlantName: ", plantName)
                val model = RoseDiseaseModel.newInstance(requireContext())

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                val inputFeature = disease(inputFeature0, imageBitmap)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                Log.e("ouputs", outputs.toString())

                //get the highest confidence amongst the prediction
                val confidences = outputFeature0.floatArray
                Log.e("confidences:", confidences.toString())
                var maxPos = 0
                var maxConfidence = 0.0f
                for(i in 0 until confidences.size) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                val classes = arrayOf("Black Spots/Leaf Scars", "Downy Mildew", "Healthy Leaf")

                val resultText = classes[maxPos]
                Log.e("class:", maxPos.toString())
                Log.e("result text:", resultText)

                val confidence = confidences[maxPos] * 100
                Log.e("confidence:", confidence.toString())

                Log.e("filePath", filePath.toString())
                val intent = Intent (activity, Diagnosis_page::class.java)
                intent.putExtra("plant_image", filePath)
                intent.putExtra("disease_name", resultText)
                intent.putExtra("confidence", confidence.toString())
                activity?.startActivity(intent)

                // Releases model resources if no longer used.
                model.close()

            }
            "Sunflower" -> {
                Log.e("PlantName: ", plantName)
                val model = SunflowerDiseaseModel.newInstance(requireContext())

                // Creates inputs for reference.
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
                val inputFeature = disease(inputFeature0, imageBitmap)

                // Runs model inference and gets result.
                val outputs = model.process(inputFeature)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                Log.e("ouputs", outputs.toString())

                //get the highest confidence amongst the prediction
                val confidences = outputFeature0.floatArray
                Log.e("confidences:", confidences.toString())
                var maxPos = 0
                var maxConfidence = 0.0f
                for(i in 0 until confidences.size) {
                    if (confidences[i] > maxConfidence) {
                        maxConfidence = confidences[i]
                        maxPos = i
                    }
                }

                val classes = arrayOf("Downy Mildew", "Healthy Leaf", "Black Spots/Leaf Scars")

                val resultText = classes[maxPos]
                Log.e("class:", maxPos.toString())
                Log.e("result text:", resultText)

                val confidence = confidences[maxPos] * 100
                Log.e("confidence:", confidence.toString())

                Log.e("filePath", filePath.toString())
                val intent = Intent (activity, Diagnosis_page::class.java)
                intent.putExtra("plant_image", filePath)
                intent.putExtra("disease_name", resultText)
                intent.putExtra("confidence", confidence.toString())
                activity?.startActivity(intent)

                // Releases model resources if no longer used.
                model.close()

            }
        }
    }


    private fun disease(imageInputFeature0: TensorBuffer, imageBitmap: Bitmap): TensorBuffer {

        // Creates inputs for reference.
        val inputFeature0 = imageInputFeature0

        //allocation of image to memory
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        //get pixels in the picture and store it into array
        val intValues = IntArray(imageSize * imageSize)
        imageBitmap.getPixels(intValues, 0, imageBitmap.width, 0,0, imageBitmap.width, imageBitmap.height)

        //center the image
        var pixel = 0
        for ( i in 0 until imageSize){
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value ushr 16) and 0xFF) * (1.0f / 255.0f))
                byteBuffer.putFloat(((value ushr 8) and 0xFF) * (1.0f / 255.0f))
                byteBuffer.putFloat((value and 0xFF) * (1.0f / 255.0f))
            }
        }

        //load buffer to image before the image is fed to the model
        inputFeature0.loadBuffer(byteBuffer)
        return inputFeature0

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.kebab, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.kebab_settings){
            //do action here
            Toast.makeText(activity, "Settings Coming Soon", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.kebab_rate){
            //do action here
            Toast.makeText(activity, "Rate Coming Soon", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.kebab_share){
            //do action here
            Toast.makeText(activity, "Share Coming Soon", Toast.LENGTH_SHORT).show()
        }

        if (id == R.id.kebab_Tutorial) {
            //do action here
            Toast.makeText(activity, "Tutorial Coming Soon", Toast.LENGTH_SHORT).show()
        }

        //logout
        if (id == R.id.logout){
            val dialog = AlertDialog.Builder(activity)
                .setTitle("Logout")
                .setMessage("Are you sure to leave?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null)
                .show()

            val currentID = FirebaseAuth.getInstance().uid
            FirebaseFirestore.getInstance().collection("profile").document(currentID.toString()).collection("presence")
                .document(currentID.toString()).update("Presence", "Offline")

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                Log.i("Dashboard_Fragment: ", "Clicked on positive button!")
                // Logout the user
                Firebase.auth.signOut()
                Toast.makeText(activity, "Logged out", Toast.LENGTH_SHORT).show()
                val logoutIntent = Intent(activity, Login_page::class.java)
                logoutIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(logoutIntent)
                activity?.finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun putFragmentInView(fragment: Fragment){
        if (fragment != null){
            val transaction = childFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_Container_View, fragment, fragment.javaClass.name)
                transaction.commitAllowingStateLoss()
        }
    }

    private fun putFragmentInView2(fragment: Fragment){
        if (fragment != null){
            val transaction = childFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_Container_View_2, fragment, fragment.javaClass.name)
            transaction.commitAllowingStateLoss()
        }
    }


}