package com.pedrenareyes.raychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NewMessageActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var startChatButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        emailEditText = findViewById(R.id.emailEditText)
        startChatButton = findViewById(R.id.startChatButton)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        startChatButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                Log.d("NewMessageActivity", "Start chat with email: $email")
                findUserByEmail(email)
            } else {
                Toast.makeText(this, "Enter an email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findUserByEmail(email: String) {
        Log.d("NewMessageActivity", "Finding user by email: $email") // Added Log
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("NewMessageActivity", "findUserByEmail onDataChange called") // Added Log
                if (snapshot.exists()) {
                    Log.d("NewMessageActivity", "User found with email: $email") // Added Log
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key
                        if (userId != null) {
                            Log.d("NewMessageActivity", "User ID found: $userId") // Added Log
                            startChat(userId)
                            return
                        }
                    }
                    Log.w("NewMessageActivity", "User found, but no UID retrieved") // Added Log
                } else {
                    Log.d("NewMessageActivity", "User not found, creating new user") // Added Log
                    createNewUser(email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NewMessageActivity", "Database error: ${error.message}")
                Toast.makeText(this@NewMessageActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createNewUser(email: String) {
        Log.d("NewMessageActivity", "Creating new user with email: $email") // Added Log
        val userId = usersRef.push().key

        if (userId != null) {
            usersRef.child(userId).child("email").setValue(email)
                .addOnSuccessListener {
                    Log.d("NewMessageActivity", "User added to database: $email, UID: $userId") // Added Log
                    Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                    startChat(userId)
                }
                .addOnFailureListener {
                    Log.e("NewMessageActivity", "Failed to add user to database: ${it.message}")
                    Toast.makeText(this, "Failed to create user.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startChat(userId: String) {
        Log.d("NewMessageActivity", "Starting ChatActivity with userId: $userId") // Added Log
        val intent = Intent(this@NewMessageActivity, ChatActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}