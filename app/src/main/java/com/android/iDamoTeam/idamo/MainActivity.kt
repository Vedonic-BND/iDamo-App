package com.android.iDamoTeam.idamo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.android.iDamoTeam.idamo.fragments.CommunityFragment
import com.android.iDamoTeam.idamo.fragments.DashboardFragment
import com.android.iDamoTeam.idamo.fragments.YouFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    private val dashboardFragment = DashboardFragment()
    private val communityFragment = CommunityFragment()
    private val youFragment = YouFragment()

    private lateinit var auth: FirebaseAuth
    private lateinit var Database: FirebaseFirestore

    private var navigationClickedTime: Long = 0
    private val MIN_CLICK_INTERVAL: Long = 750
    private var currentFragment: Fragment? = null

    private companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        Database = FirebaseFirestore.getInstance()

        replaceFragment(dashboardFragment)

        var bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> replaceFragment(dashboardFragment)
                R.id.nav_community -> replaceFragment(communityFragment)
                R.id.nav_you -> replaceFragment(youFragment)
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            Log.w(MainActivity.TAG, "User is not signed in. Please sign in to proceed!")
            startActivity(Intent(this, Login_page::class.java))
            finish()
        }

    }


    private fun replaceFragment(fragment: Fragment){
        val currentTime = System.currentTimeMillis()
        if(fragment != null){
            if (currentTime - navigationClickedTime > MIN_CLICK_INTERVAL) {
                navigationClickedTime = currentTime
                val transaction = supportFragmentManager.beginTransaction()
//                if (currentFragment != null) {
//                    transaction.hide(currentFragment!!)
//                }
                transaction.replace(R.id.bottom_nav_fragment_container, fragment)
                transaction.commit()
                currentFragment = fragment
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentID = FirebaseAuth.getInstance().uid
        val map = HashMap<String, Any>()
        map["Presence"] = "Online"
        Database.collection("profile").document(currentID.toString()).collection("presence")
            .document(currentID.toString()).set(map)

    }

    override fun onPause() {
        super.onPause()
        val currentID = FirebaseAuth.getInstance().uid
        val map = HashMap<String, Any>()
        map["Presence"] = "Offline"
        Database.collection("profile").document(currentID.toString()).collection("presence")
            .document(currentID.toString()).set(map)

    }
}