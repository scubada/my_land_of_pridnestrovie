package com.sgk.myworldmogilev.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sgk.myworldmogilev.views.fragments.welcomeFragments.*

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
            Fragment2Fragment()
        }
        1 -> {
            Fragment3Fragment()
        }
        2 -> {
            Fragment4Fragment()
        }
        else -> {
            Fragment2Fragment()
        }
    }
}