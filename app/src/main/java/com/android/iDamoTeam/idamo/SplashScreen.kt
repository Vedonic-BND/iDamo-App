package com.android.iDamoTeam.idamo

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import androidx.appcompat.app.AlertDialog

class SplashScreen : AppCompatActivity() {

    private var SS_time : Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()



        if (!isNetworkAvailable(this)) {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning)
                .setTitle("Internet Connection Alert")
                .setMessage("Please Check Your Internet Connection")
                .setPositiveButton(
                    "Close"
                ) { dialogInterface, i -> finish() }.show()
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, Login_page::class.java))
                finish()
            }, SS_time)
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected


    }
}