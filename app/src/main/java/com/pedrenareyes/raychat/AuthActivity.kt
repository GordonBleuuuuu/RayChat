package com.pedrenareyes.raychat

import android.os.Bundle
import android.util.Log // Import Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.pedrenareyes.raychat.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        Log.d("AuthActivity", "onCreate called") // Log activity creation

        // Initialize Firebase here
        Log.d("AuthActivity", "Initializing FirebaseApp")
        FirebaseApp.initializeApp(this)
        Log.d("AuthActivity", "FirebaseApp initialized")

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        Log.d("AuthActivity", "Creating AuthPagerAdapter")
        val adapter = AuthPagerAdapter(this)
        viewPager.adapter = adapter
        Log.d("AuthActivity", "AuthPagerAdapter created")

        Log.d("AuthActivity", "Attaching TabLayoutMediator")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Login" else "Sign Up"
            Log.d("AuthActivity", "Tab created: ${tab.text}, Position: $position")
        }.attach()
        Log.d("AuthActivity", "TabLayoutMediator attached")
    }
}