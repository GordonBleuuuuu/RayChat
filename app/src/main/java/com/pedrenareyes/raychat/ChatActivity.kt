package com.pedrenareyes.raychat

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pedrenareyes.raychat.R

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var chatListView: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var backButton: Button
    private lateinit var chatMessages: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var otherUserId: String
    private lateinit var currentUserId: String // Added for hardcoded user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        Log.d("ChatActivity", "onCreate called")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("messages")
        chatListView = findViewById(R.id.chatListView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        backButton = findViewById(R.id.backButton)

        chatMessages = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chatMessages)
        chatListView.adapter = adapter

        // Hardcoded User IDs for Testing
        currentUserId = "kenpedrena@gmail.com"  // Replace with a known user ID from your database
        otherUserId = "reyesjosephmarting@gmail.com"    // Replace with another known user ID from your database

        Log.d("ChatActivity", "Hardcoded Chat with userId: $otherUserId")

        val chatId = if (currentUserId > otherUserId) {
            "$currentUserId-$otherUserId"
        } else {
            "$otherUserId-$currentUserId"
        }

        database.child(chatId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.child("message").getValue(String::class.java)
                val senderId = snapshot.child("sender").getValue(String::class.java)
                if (message != null && senderId != null) {
                    val displayMessage = if (senderId == currentUserId) {
                        "You: $message"
                    } else {
                        "Other User: $message" // Simplified for hardcoded
                    }
                    chatMessages.add(displayMessage)
                    adapter.notifyDataSetChanged()
                    chatListView.setSelection(chatMessages.size - 1)
                    Log.d("ChatActivity", "Message added: $displayMessage")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("ChatActivity", "Message changed")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("ChatActivity", "Message removed")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("ChatActivity", "Message moved")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatActivity", "Database error: ${error.message}")
                Toast.makeText(this@ChatActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                val messageData = HashMap<String, String>()
                messageData["sender"] = currentUserId
                messageData["message"] = message

                database.child(chatId).push().setValue(messageData)
                    .addOnSuccessListener {
                        messageEditText.text.clear()
                        Log.d("ChatActivity", "Message sent: $message")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ChatActivity", "Failed to send message: ${exception.message}")
                        Toast.makeText(this@ChatActivity, "Failed to send message.", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(this@ChatActivity, "Enter a message.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            Log.d("ChatActivity", "Back button pressed")
            finish()
        }
    }
}
