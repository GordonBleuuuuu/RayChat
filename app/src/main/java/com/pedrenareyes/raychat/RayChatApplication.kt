package com.pedrenareyes.raychat

import android.app.Application
import android.util.Log // Import Log
import com.google.firebase.FirebaseApp

class RayChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("RayChatApplication", "Application onCreate called") // Log application start

        try {
            Log.d("RayChatApplication", "Initializing FirebaseApp")
            FirebaseApp.initializeApp(this)
            Log.d("RayChatApplication", "FirebaseApp initialized successfully")
        } catch (e: Exception) {
            Log.e("RayChatApplication", "Failed to initialize FirebaseApp: ${e.message}")
        }
    }
}