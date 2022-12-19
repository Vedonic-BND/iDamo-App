package com.android.vedonic.idamo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.vedonic.idamo.Adapter.WeatherAdapter
import com.android.vedonic.idamo.model.Weather
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_weather_forecast.*
import org.json.JSONException

class WeatherForecast : AppCompatActivity() {

    private lateinit var relativeLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var cityName: TextView
    private lateinit var icon: ImageView
    private lateinit var temperature: TextView
    private lateinit var condition: TextView
    private lateinit var weatherBackground: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var mWeather: MutableList<Weather>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_forecast)

        relativeLayout = findViewById(R.id.weatherRL)
        progressBar = findViewById(R.id.weatherProgressBar)
        cityName = findViewById(R.id.weatherLocation)
        icon = findViewById(R.id.icon)
        weatherBackground = findViewById(R.id.weatherBackground)
        temperature = findViewById(R.id.temperature)
        condition = findViewById(R.id.currentState)


        recyclerView = findViewById(R.id.weatherRecycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mWeather = ArrayList()
        weatherAdapter = WeatherAdapter(this, mWeather as ArrayList<Weather>)
        recyclerView.adapter = weatherAdapter

        val intent = intent
        val currentCity = intent.getStringExtra("location")

        getWeatherInfo(currentCity!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getWeatherInfo(currentCity: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=b5f43c59f94c461fa6e83558221612&q="+ currentCity +"&days=1&aqi=no&alerts=no"
        cityName.text = currentCity
        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            val str = response.toString()
            progressBar.visibility = View.GONE
            relativeLayout.visibility = View.VISIBLE
            mWeather.clear()

            try {
                val temp = response.getJSONObject("current").getString("temp_c")
                temperature.text = temp + "°c"
                val isDay = response.getJSONObject("current").getInt("is_day")
                val conditionText = response.getJSONObject("current").getJSONObject("condition").getString("text")
                val conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon")
                Picasso.get().load("https:"+conditionIcon).into(icon)
                condition.text = conditionText.capitalize()

                if (isDay==1){
                    weatherBackground.setBackgroundResource(R.drawable.day)
                }else {
                    weatherBackground.setBackgroundResource(R.drawable.night)
                }

                val forcastOBJ = response.getJSONObject("forecast")
                val forcastArr0 = forcastOBJ.getJSONArray("forecastday").getJSONObject(0)
                val hourArr = forcastArr0.getJSONArray("hour")

                for (item in 0 until hourArr.length()) {
                    val hourOBJ = hourArr.getJSONObject(item)
                    val time = hourOBJ.getString("time")
                    val temp = hourOBJ.getString("temp_c")
                    val img = hourOBJ.getJSONObject("condition").getString("icon")
                    val con = hourOBJ.getJSONObject("condition").getString("text")

                    mWeather.add(Weather(time, temp, img, con ))
                }
                weatherAdapter.notifyDataSetChanged()

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.d("Response", str)
        }, { error ->

            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            Log.d("Response", error.message.toString())
        })

        requestQueue.add(jsonObjectRequest)

    }
}
