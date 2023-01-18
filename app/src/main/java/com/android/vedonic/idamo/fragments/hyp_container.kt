package com.android.vedonic.idamo.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
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
import com.android.vedonic.idamo.Diagnosis_page
import com.android.vedonic.idamo.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class hyp_container : Fragment() {
    private val IMAGE_CAPTURE_CODE: Int = 1001

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        openCamera(requestCode, resultCode, data)
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
                val imageBitmap = data.extras?.get("data") as Bitmap
                Log.e("imageBitmap", imageBitmap.toString())
                val stream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val resolver = requireActivity().contentResolver
                val date = System.currentTimeMillis()
                val path: String = MediaStore.Images.Media.insertImage(resolver, imageBitmap,
                    "IMG_$date", null)
                val imageUri = Uri.parse(path)
                Log.e("ImageUri", imageUri.toString())
//
//                val imageBitmap = data.extras?.get("data") as Bitmap
//                val stream = ByteArrayOutputStream()
//                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                val resolver = requireActivity().applicationContext.contentResolver
//                val path: String = MediaStore.Images.Media.insertImage(resolver, imageBitmap, "Title", null)
//
//                val imageUri = Uri.parse(path)
//                val calendar = Calendar.getInstance()
//                val ref = storage?.reference?.child("chats")?.child(calendar.timeInMillis.toString()+"")
//                dialog!!.show()


                val passintent = Intent(activity, Diagnosis_page::class.java)
                passintent.putExtra("plant_image", imageUri)
                startActivity(passintent)

            }else{
                Toast.makeText(requireContext(), "Error Loading Image. Please Try Again!", Toast.LENGTH_SHORT).show()
            }

        }
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