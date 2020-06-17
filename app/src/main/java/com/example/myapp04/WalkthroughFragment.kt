package com.example.myapp04

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.myapp04.walkthrough.ViewAdapter
import com.example.myapp04.walkthrough.ViewItem

/**
 * A simple [Fragment] subclass.
 */
class WalkthroughFragment : Fragment() {

    private lateinit var walkthroughAdapter: ViewAdapter
    private lateinit var viewPager: ViewPager2

    private lateinit var indicatorList: List<ImageView>

    private lateinit var goBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var skipBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walkthrough, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // handle button
        nextBtn = view.findViewById<Button>(R.id.nextBtn)
        skipBtn = view.findViewById<Button>(R.id.skipBtn)
        goBtn = view.findViewById<Button>(R.id.goBtn)

        // Create List of Page
        val walkthroughList = listOf<ViewItem>(
            ViewItem("Please wear a face mask", R.raw.mask),
            ViewItem("And always keep clean your hand", R.raw.hand),
            ViewItem("Should stay at home", R.raw.home)
        )

        // Create ViewAdapter with that list
        walkthroughAdapter = ViewAdapter(walkthroughList)

        // Assign Adapter to PageViewer Adapter. Done \(^_^)/
        viewPager = view.findViewById<ViewPager2>(R.id.viewPager2)
        viewPager.adapter = walkthroughAdapter

        // handle Indicator

        val indicator1 = view.findViewById<ImageView>(R.id.indicator1)
        val indicator2 = view.findViewById<ImageView>(R.id.indicator2)
        val indicator3 = view.findViewById<ImageView>(R.id.indicator3)

        indicatorList = listOf(
            indicator1,
            indicator2,
            indicator3
        )

        setCurrentIndicator(viewPager.currentItem)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        nextBtn.setOnClickListener{
            if (viewPager.currentItem + 1 < walkthroughAdapter.itemCount) {
                viewPager.currentItem += 1
            }
        }

        goBtn.setOnClickListener{
            findNavController()?.navigate(R.id.action_walkthroughFragment_to_mapFragment)
        }

        skipBtn.setOnClickListener{
            findNavController()?.navigate(R.id.action_walkthroughFragment_to_mapFragment)
        }
    }

    fun setCurrentIndicator(index: Int) {
        for (i in indicatorList.indices) {
            if (i == index) {
                indicatorList[i].setImageDrawable(
                    ContextCompat.getDrawable(context!!, R.drawable.indicator_active)
                )
            } else {
                indicatorList[i].setImageDrawable(
                    ContextCompat.getDrawable(context!!, R.drawable.indicator_inactive)
                )
            }
        }

        // check last page
        if (index == walkthroughAdapter.itemCount - 1) {
            nextBtn.visibility = View.GONE
            skipBtn.visibility = View.GONE
            goBtn.visibility = View.VISIBLE
        } else {
            nextBtn.visibility = View.VISIBLE
            skipBtn.visibility = View.VISIBLE
            goBtn.visibility = View.GONE
        }
    }

}