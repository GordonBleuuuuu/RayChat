package com.pedrenareyes.raychat

import android.os.Bundle
import android.util.Log // Import Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pedrenareyes.raychat.R

class UserProfileActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        Log.d("UserProfileActivity", "onCreate called") // Log activity creation

        nameEditText = findViewById(R.id.nameEditText)
        bioEditText = findViewById(R.id.bioEditText)
        saveButton = findViewById(R.id.saveButton)

        database = FirebaseDatabase.getInstance().getReference("users")
        auth = FirebaseAuth.getInstance()

        saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun saveUserProfile() {
        val name = nameEditText.text.toString()
        val bio = bioEditText.text.toString()
        val userId = auth.currentUser?.uid

        Log.d("UserProfileActivity", "Saving profile: Name=$name, Bio=$bio, UserId=$userId") // Log profile data

        if (userId != null && name.isNotEmpty()) {
            val userProfile = HashMap<String, Any>()
            userProfile["name"] = name
            userProfile["bio"] = bio

            database.child(userId).updateChildren(userProfile)
                .addOnSuccessListener {
                    Log.d("UserProfileActivity", "Profile saved successfully") // Log success
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                    finish() // Go back
                }
                .addOnFailureListener { exception ->
                    Log.e("UserProfileActivity", "Failed to save profile: ${exception.message}") // Log failure
                    Toast.makeText(this, "Failed to save profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
        }
    }
}