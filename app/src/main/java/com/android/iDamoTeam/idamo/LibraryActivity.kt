package com.android.iDamoTeam.idamo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.iDamoTeam.idamo.databinding.ActivityLibraryBinding
import com.android.iDamoTeam.idamo.databinding.DiagnosisPageBinding
import com.android.iDamoTeam.idamo.databinding.FragmentInnerLibraryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.diagnosis_page.*
import kotlinx.android.synthetic.main.fragment_description.*
import org.w3c.dom.Text

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
            tab.setCustomView(R.layout.custom_tab_item)
            val tabTextView = tab.customView?.findViewById<TextView>(R.id.tab_text)
            tabTextView?.text = fragmentDataList[position].title

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null && tab.position == position) {
                        tabTextView?.setTextColor(resources.getColor(R.color.white))
                        tabTextView?.setTypeface(null, Typeface.BOLD)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    if (tab != null && tab.position == position) {
                        tabTextView?.setTextColor(resources.getColor(R.color.grey))
                        tabTextView?.setTypeface(null, Typeface.NORMAL)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

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
                0 -> ViewPagerFragment().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                1 -> ViewPagerFragment().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                2 -> ViewPagerFragment().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                3 -> ViewPagerFragment().apply {
                    arguments = Bundle().apply {
                        putString("diseaseName", fragmentDataList[position].title)
                    }
                }
                else -> throw RuntimeException("Invalid position: $position")
            }
        }

    }

    class ViewPagerFragment: Fragment(R.layout.fragment_inner_library) {
        private val diseaseName: String by lazy {
            requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
        }

        private lateinit var binding: FragmentInnerLibraryBinding

        data class FragmentData(val title: String, val disName: String)

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = FragmentInnerLibraryBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val fragmentDataList =
                if (diseaseName != "Healthy Leaf") {
                    listOf<FragmentData>(
                        FragmentData(
                            "Description",
                            "$diseaseName"
                        ),
                        FragmentData("Symptoms", "$diseaseName"),
                        FragmentData("Solution", "$diseaseName"),
                        FragmentData("Video", "$diseaseName")
                    )
                } else {
                    listOf<FragmentData>(
                        FragmentData(
                            "Description",
                            "$diseaseName"
                        ),
                        FragmentData("Solution", "$diseaseName"),
                        FragmentData("Video", "$diseaseName")
                    )
                }

            val tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = fragmentDataList[position].title
            }

            binding.viewPager.adapter = ViewPagerFragmentAdapter(this, fragmentDataList)
            tabLayoutMediator.attach()

            when (diseaseName) {
                "Downy Mildew" -> {
                    view.findViewById<ImageView>(R.id.diseaseImage).setImageResource(R.drawable.downey_mildew)
                }
                "Black Spots/Leaf Scars" -> {
                    view.findViewById<ImageView>(R.id.diseaseImage).setImageResource(R.drawable.spots_scars)
                }
                "Shot Hole" -> {
                    view.findViewById<ImageView>(R.id.diseaseImage).setImageResource(R.drawable.shot_hole)
                }
                "Healthy Leaf" -> {
                    view.findViewById<ImageView>(R.id.diseaseImage).setImageResource(R.drawable.healthy)
                }
            }

        }

        class ViewPagerFragmentAdapter(viewPagerFragment: ViewPagerFragment,
                                       private val fragmentDataList: List<FragmentData>
        ) : FragmentStateAdapter(viewPagerFragment) {
            override fun getItemCount(): Int {
                Log.e("fragmentDataList SIZE", fragmentDataList.size.toString())
                return fragmentDataList.size
            }

            override fun createFragment(position: Int): Fragment {
                if (fragmentDataList.size == 4) {
                    return when(position) {
                        0 -> ViewPagerFragmentDescription().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        1 -> ViewPagerFragmentSymptoms().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        2 -> ViewPagerFragmentSolution().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        3 -> ViewPagerFragmentVideo().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        else -> throw RuntimeException("Invalid position: $position")
                    }
                }else{
                    return when(position) {
                        0 -> ViewPagerFragmentDescription().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        1 ->  ViewPagerFragmentSolution().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        2 -> ViewPagerFragmentVideo().apply {
                            arguments = Bundle().apply {
                                putString("diseaseName", fragmentDataList[position].disName)
                            }
                        }
                        else -> throw RuntimeException("Invalid position: $position")
                    }
                }
            }
        }

        class ViewPagerFragmentDescription: Fragment(R.layout.fragment_description) {
            private val diseaseName: String by lazy {
                requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
            }

            @SuppressLint("SetTextI18n")
            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                when (diseaseName) {
                    "Downy Mildew" -> {
                        view.findViewById<TextView>(R.id.diseaseName)
                            .setText(R.string.downyMildewDiseaseTitle)
                        view.findViewById<TextView>(R.id.confidence).visibility = View.GONE
                        view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.downyMildewDiseaseDescription)
                        view.findViewById<TextView>(R.id.diseaseDesc).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Black Spots/Leaf Scars" -> {
                        view.findViewById<TextView>(R.id.diseaseName)
                            .setText(R.string.scarsSpotsDiseaseTitle)
                        view.findViewById<TextView>(R.id.confidence).visibility = View.GONE
                        view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.scarsSpotsDiseaseDescription)
                        view.findViewById<TextView>(R.id.diseaseDesc).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Shot Hole" -> {
                        view.findViewById<TextView>(R.id.diseaseName)
                            .setText(R.string.shotHoleDiseaseTitle)
                        view.findViewById<TextView>(R.id.confidence).visibility = View.GONE
                        view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.shotHoleDiseaseDescription)
                        view.findViewById<TextView>(R.id.diseaseDesc).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Healthy Leaf" -> {
                        view.findViewById<TextView>(R.id.diseaseName).setText(R.string.healthyLeafTitle)
                        view.findViewById<TextView>(R.id.confidence).visibility = View.GONE
                        view.findViewById<TextView>(R.id.diseaseDesc).setText(R.string.healthyLeafDescription)
                        view.findViewById<TextView>(R.id.diseaseDesc).movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
        }

        class ViewPagerFragmentSymptoms: Fragment(R.layout.fragment_symptoms) {
            private val diseaseName: String by lazy {
                requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                when (diseaseName) {
                    "Downy Mildew" -> {
                        view.findViewById<TextView>(R.id.symptoms).setText(R.string.downyMildewDiseaseSymptoms)
                        view.findViewById<TextView>(R.id.symptoms).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Black Spots/Leaf Scars" -> {
                        view.findViewById<TextView>(R.id.symptoms).setText(R.string.scarsSpotsDiseaseSymptoms)
                        view.findViewById<TextView>(R.id.symptoms).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Shot Hole" -> {
                        view.findViewById<TextView>(R.id.symptoms).setText(R.string.shotHoleDiseaseSymptoms)
                        view.findViewById<TextView>(R.id.symptoms).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Healthy Leaf" -> {
                        view.findViewById<TextView>(R.id.symptomsTitle).visibility = View.GONE
                        view.findViewById<TextView>(R.id.symptoms).visibility = View.GONE
                    }
                }
            }
        }

        class ViewPagerFragmentSolution: Fragment(R.layout.fragment_solution) {
            private val diseaseName: String by lazy {
                requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                when (diseaseName) {
                    "Downy Mildew" -> {
                        view.findViewById<TextView>(R.id.solution).setText(R.string.downyMildewDiseaseSolution)
                        view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Black Spots/Leaf Scars" -> {
                        view.findViewById<TextView>(R.id.solution).setText(R.string.scarsSpotsDiseaseSolution)
                        view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Shot Hole" -> {
                        view.findViewById<TextView>(R.id.solution).setText(R.string.shotHoleDiseaseSolution)
                        view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                    }
                    "Healthy Leaf" -> {
                        view.findViewById<TextView>(R.id.solution).setText(R.string.healthyLeafDiseaseSolution)
                        view.findViewById<TextView>(R.id.solution).movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
        }
        class ViewPagerFragmentVideo: Fragment(R.layout.fragment_video) {
            private val diseaseName: String by lazy {
                requireArguments().getString("diseaseName") ?: "There's some kind of error. Please try again later."
            }

            @SuppressLint("SetTextI18n", "CutPasteId")
            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                when (diseaseName) {
                    "Downy Mildew" -> {
                        view.findViewById<TextView>(R.id.firstVideoTitle).text = "Baking Soda Solution:"

                        val ytSolutionFirst: YouTubePlayerView = view.findViewById(R.id.ytSolutionFirst)
                        lifecycle.addObserver(ytSolutionFirst)

                        ytSolutionFirst.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "2i6giJc97ws"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.secondVideoTitle).text = "Milk Solution:"

                        val ytSolutionSecond: YouTubePlayerView = view.findViewById(R.id.ytSolutionSecond)
                        lifecycle.addObserver(ytSolutionSecond)

                        ytSolutionSecond.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "320D-41xt-M"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.thirdVideoTitle).text = "Neem Oil:"

                        val ytSolutionThird: YouTubePlayerView = view.findViewById(R.id.ytSolutionThird)
                        lifecycle.addObserver(ytSolutionThird)

                        ytSolutionThird.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "u9AIuIsnEGs"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })
                    }
                    "Black Spots/Leaf Scars" -> {
                        view.findViewById<TextView>(R.id.firstVideoTitle).text = "Dairy Milk Solution:"

                        val ytSolutionFirst: YouTubePlayerView = view.findViewById(R.id.ytSolutionFirst)
                        lifecycle.addObserver(ytSolutionFirst)

                        ytSolutionFirst.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "2i6giJc97ws"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.secondVideoTitle).text = "Baking Soda Solution:"

                        val ytSolutionSecond: YouTubePlayerView = view.findViewById(R.id.ytSolutionSecond)
                        lifecycle.addObserver(ytSolutionSecond)

                        ytSolutionSecond.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "320D-41xt-M"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.thirdVideoTitle).text = "Neem Oil:"

                        val ytSolutionThird: YouTubePlayerView = view.findViewById(R.id.ytSolutionThird)
                        lifecycle.addObserver(ytSolutionThird)

                        ytSolutionThird.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "u9AIuIsnEGs"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })

                    }
                    "Shot Hole" -> {
                        view.findViewById<TextView>(R.id.firstVideoTitle).text = "Fungicidal Solution:"

                        val ytSolutionFirst: YouTubePlayerView = view.findViewById(R.id.ytSolutionFirst)
                        lifecycle.addObserver(ytSolutionFirst)

                        ytSolutionFirst.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "9vcblls4agc"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.secondVideoTitle).text = "Baking Soda Solution:"

                        val ytSolutionSecond: YouTubePlayerView = view.findViewById(R.id.ytSolutionSecond)
                        lifecycle.addObserver(ytSolutionSecond)

                        ytSolutionSecond.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "320D-41xt-M"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.thirdVideoTitle).text = "Neem Oil:"

                        val ytSolutionThird: YouTubePlayerView = view.findViewById(R.id.ytSolutionThird)
                        lifecycle.addObserver(ytSolutionThird)

                        ytSolutionThird.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "u9AIuIsnEGs"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })
                    }
                    "Healthy Leaf" -> {
                        view.findViewById<TextView>(R.id.firstVideoTitle).text = "When and How to Prune:"

                        val ytSolutionFirst: YouTubePlayerView = view.findViewById(R.id.ytSolutionFirst)
                        lifecycle.addObserver(ytSolutionFirst)

                        ytSolutionFirst.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "dQn_MGt-ZRQ"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })


                        view.findViewById<TextView>(R.id.secondVideoTitle).text = "Plant Care:"

                        val ytSolutionSecond: YouTubePlayerView = view.findViewById(R.id.ytSolutionSecond)
                        lifecycle.addObserver(ytSolutionSecond)

                        ytSolutionSecond.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                                val videoId = "J1LW1Nz0pd4"
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        })

                        view.findViewById<TextView>(R.id.thirdVideoTitle).visibility = View.GONE
                        view.findViewById<YouTubePlayerView>(R.id.ytSolutionThird).visibility = View.GONE
                    }
                }
            }
        }
    }
}