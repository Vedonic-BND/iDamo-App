package com.android.iDamoTeam.idamo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.iDamoTeam.idamo.LibraryActivity
import com.android.iDamoTeam.idamo.R
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var imageArray: ArrayList<Int> = ArrayList()
    var carouselView: CarouselView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        imageArray.add(R.drawable.dis_lib)
        imageArray.add(R.drawable.home_rem)
        imageArray.add(R.drawable.watch_vid)


        carouselView = view.findViewById(R.id.carouselView)
        carouselView!!.pageCount = imageArray.size
        carouselView!!.setImageListener(imageListener)

        carouselView!!.setImageClickListener(ImageClickListener { position ->
            val intent = Intent(requireActivity(), LibraryActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
        })

        return view
    }


    var imageListener = ImageListener { position, imageView ->
        imageView.setImageResource(imageArray[position])
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LibraryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LibraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}