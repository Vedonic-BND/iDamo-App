package com.android.vedonic.idamo.fragments

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.android.vedonic.idamo.Diagnosis_page
import com.android.vedonic.idamo.R
import com.android.vedonic.idamo.ml.*
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class hyp_container : Fragment() {
    private val IMAGE_CAPTURE_CODE: Int = 1001

    private var param1: String? = null
    private var param2: String? = null

    var imageSize = 224


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
            openCamera(requestCode, resultCode, data)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_hyp_container, container, false)
        val take_a_picture_btn = v.findViewById<Button>(R.id.take_a_picture_btn)
        take_a_picture_btn.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Unable to open camera. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }



        return v
    }



    private fun openCamera (requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var imageBitmap = data.extras?.get("data") as Bitmap
                Log.e("imageBitmap: ", imageBitmap.toString())

                var dimension = Math.min(imageBitmap.width, imageBitmap.height)
                imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension)
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false)


                var image = data.extras?.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val resolver = requireActivity().contentResolver
                val date = System.currentTimeMillis()


                val path: String = MediaStore.Images.Media.insertImage(resolver, image,
                    "IMG_$date", null)

                Log.e("path", path)
                val filePath = Uri.parse(path)



                classifyImage(imageBitmap, filePath)


//                Log.e("imageUri", imageUri.toString())
//                val passintent = Intent(activity, Diagnosis_page::class.java)
//                passintent.putExtra("plant_image", imageUri)
//                startActivity(passintent)



            }else{
                Toast.makeText(requireContext(), "Error Loading Image. Please Try Again!", Toast.LENGTH_SHORT).show()
            }

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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            hyp_container().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}