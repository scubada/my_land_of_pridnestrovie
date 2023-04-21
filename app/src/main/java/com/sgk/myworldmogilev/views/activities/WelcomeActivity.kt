package com.sgk.myworldmogilev.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.adapters.PageAdapter
import com.sgk.myworldmogilev.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sp = getSharedPreferences("sp", MODE_PRIVATE)

        viewPager()

        binding.buttonok.setOnClickListener {
            when (viewPager.currentItem) {
                2 -> {
                    sp.edit().putString("splash", "ok").apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                }
                1 -> viewPager.currentItem = 4
                0 -> viewPager.currentItem = 4
            }
        }
    }

    override fun onBackPressed() {
        when (viewPager.currentItem) {
            0 -> finishAffinity()
            1 -> viewPager.currentItem = 0
            2 -> viewPager.currentItem = 1
        }
    }

    private fun viewPager() {
        viewPager = binding.viewPager
        viewPager.adapter = PageAdapter(supportFragmentManager)
        viewPager.currentItem = 0
        viewPager.overScrollMode = View.OVER_SCROLL_NEVER
        binding.springDotsIndicator.attachTo(viewPager)

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            @SuppressLint("ObjectAnimatorBinding")
            override fun onPageSelected(position: Int) {
                when (viewPager.currentItem) {
                    0 -> {
                        binding.textChoice.text = getString(R.string.skip)
                    }
                    1 -> {
                        binding.textChoice.text = getString(R.string.skip)
                    }
                    2 -> {
                        binding.textChoice.text = getString(R.string.play)
                    }
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}