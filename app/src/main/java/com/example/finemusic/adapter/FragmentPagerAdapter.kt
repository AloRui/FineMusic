package com.example.finemusic.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentPagerAdapter(
    private val lsFragment: MutableList<Pair<String, Fragment>>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return lsFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return lsFragment[position].second
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return lsFragment[position].first
    }
}