package com.android.vedonic.idamo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.vedonic.idamo.R
import com.android.vedonic.idamo.WeatherForecast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.*
import kotlinx.android.synthetic.main.fragment_weather_container.*
import kotlinx.android.synthetic.main.fragment_weather_container.temperature
import kotlinx.android.synthetic.main.weather_forecast_item.*
import org.json.JSONException
import java.text.SimpleDateFormat

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class weather_container : Fragment() {
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var currentLocation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflate = inflater.inflate(R.layout.fragment_weather_container, container, false)
        inflate.setOnClickListener{
            val intent = Intent(context, WeatherForecast::class.java)
            intent.putExtra("location", currentLocation)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up)
        }


        return inflate
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!

//                        Log.e("lat", "${list[0].latitude}")
//                        Log.e("long", "${list[0].longitude}")
//                        Log.e("country", "${list[0].countryName}")
//                        Log.e("locality", "${list[0].locality}")
//                        Log.e("adminArea", "${list[0].adminArea}")
//                        Log.e("subadminArea", "${list[0].subAdminArea}")
//                        Log.e("thoroughfare", "${list[0].thoroughfare}")
//                        Log.e("address", "${list[0].getAddressLine(0)}")

                        currentLocation = list[0].locality + " City, " + list[0].subAdminArea

                        getWeatherInfo(currentLocation)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getWeatherInfo(currentCity: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=b5f43c59f94c461fa6e83558221612&q="+ currentCity +"&days=1&aqi=no&alerts=no"
        weatherLocation.text = currentCity
        val requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->

            try {
                val temp = response.getJSONObject("current").getString("temp_c")
                temperature.text = temp
                val conditionText = response.getJSONObject("current").getJSONObject("condition").getString("text")
                val conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon")
                Picasso.get().load("https:"+conditionIcon).into(weatherIcon)
                weatherCondition.text = conditionText

                val localDate = Calendar.getInstance()
                Log.e("lcoal time", localDate.toString())
                val dateNow = SimpleDateFormat("MMM dd, yyyy")
                val dayNow = SimpleDateFormat("EEE")

                try {
                    val date = dateNow.format(localDate.time)
                    val day = dayNow.format(localDate.time)

                    weatherDate.text = "$date - $day"

                }catch (e: Exception) {
                    e.printStackTrace()
                }

                val forcastOBJ = response.getJSONObject("forecast")
                val forcastArr0 = forcastOBJ.getJSONArray("forecastday").getJSONObject(0)
                val dayArr = forcastArr0.getJSONObject("day")

                val willItRain = dayArr.getString("daily_will_it_rain")
                val rainChance = dayArr.getString("daily_chance_of_rain").toInt()

                if (willItRain == "1") {
                    if (rainChance < 20) {
                        //drizzle
                        status.text = "Just drizzle."
                    }
                    else if (rainChance < 30) {
                        //slight chance
                        status.text = "Low chance of rain."
                    }
                    else if (rainChance < 60) {
                        //chance
                        status.text = "There's a chance of rain."
                    }
                    else if (rainChance < 80) {
                        //likely
                        status.text = "Big chance of rain."
                    }
                    else if (rainChance < 100) {
                        //it will
                        status.text = "Most likely chance of rain."
                    }
                }else{
                    status.text = "No chances of rain."
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }, { error ->

            Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
            Log.d("Response", error.message.toString())
        })

        requestQueue.add(jsonObjectRequest)

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            weather_container().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}