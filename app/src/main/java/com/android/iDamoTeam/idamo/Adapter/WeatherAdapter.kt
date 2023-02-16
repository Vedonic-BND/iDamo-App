package com.android.iDamoTeam.idamo.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.iDamoTeam.idamo.R
import com.android.iDamoTeam.idamo.model.Weather
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter (private var mContext: Context,
                      private var mWeather: List<Weather>,
                      private var isFragment: Boolean = false) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.weather_forecast_item, parent, false)
        return WeatherAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mWeather.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        val weather = mWeather[position]
        holder.temperature.text = weather.temperature + "°c"
        Picasso.get().load("https:"+weather.icon).into(holder.icon)
        holder.condition.text = weather.condition
        val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val output = SimpleDateFormat("hh:mm aa")

        try {
            val t = input.parse(weather.time)
            holder.time.text = output.format(t as Date)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var time: TextView = itemView.findViewById(R.id.time)
        var temperature: TextView = itemView.findViewById(R.id.temperature)
        var condition: TextView = itemView.findViewById(R.id.condition)
        var icon: ImageView = itemView.findViewById(R.id.icon)
    }

}