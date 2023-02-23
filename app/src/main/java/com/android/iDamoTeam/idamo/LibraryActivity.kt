package com.android.iDamoTeam.idamo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.iDamoTeam.idamo.databinding.ActivityLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.diagnosis_page.*
import kotlinx.android.synthetic.main.fragment_description.*

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    data class FragmentData(val title: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val fragmentDataList =
            listOf<FragmentData>(
                FragmentData("Healthy Leaf"),
                FragmentData("Black Spots/Leaf Scars"),
                FragmentData("Downy Mildew"),
                FragmentData("Shot Hole")
            )

        val tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = fragmentDataList[position].title
        }

        binding.viewPager.adapter = LibraryAdapter(this, fragmentDataList)
        tabLayoutMediator.attach()

        done_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    class LibraryAdapter(
        activity: AppCompatActivity,
        private val fragmentDataList: List<FragmentData>
    ): FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            Log.e("fragmentDataList SIZE", fragmentDataList.size.toString())
            return fragmentDataList.size
        }

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> Diagnosis_page.ViewPagerFragmentDescription().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                1 -> Diagnosis_page.ViewPagerFragmentSymptoms().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                2 -> Diagnosis_page.ViewPagerFragmentSolution().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                3 -> Diagnosis_page.ViewPagerFragmentSolution().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                else -> throw RuntimeException("Invalid position: $position")
            }
        }

    }
}