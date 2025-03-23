package com.pedrenareyes.raychat

import android.util.Log // Import Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AuthPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        Log.d("AuthPagerAdapter", "getItemCount called, returning 2") // Log item count
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("AuthPagerAdapter", "createFragment called, position: $position")
        return when (position) {
            0 -> {
                Log.d("AuthPagerAdapter", "Creating LoginFragment")
                LoginFragment()
            }
            1 -> {
                Log.d("AuthPagerAdapter", "Creating SignupFragment")
                SignupFragment()
            }
            else -> {
                Log.e("AuthPagerAdapter", "Invalid position in createFragment: $position, defaulting to LoginFragment")
                LoginFragment() // Default to Login
            }
        }
    }
}