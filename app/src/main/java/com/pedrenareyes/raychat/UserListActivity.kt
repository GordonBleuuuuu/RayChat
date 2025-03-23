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

class UserListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userListView: ListView
    private lateinit var userList: ArrayList<String>
    private lateinit var userIds: ArrayList<String>
    private lateinit var auth: FirebaseAuth

    // Chat-related UI elements
    private lateinit var chatListView: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatMessages: ArrayList<String>
    private lateinit var chatAdapter: ArrayAdapter<String>

    private lateinit var otherUserId: String //  Hardcoded for this Activity
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list) //  Make sure your layout contains the chat elements

        Log.d("UserListActivity", "onCreate called")

        database = FirebaseDatabase.getInstance().getReference("users")
        userListView = findViewById(R.id.selectUserListView) //  For the user list (might be removed)
        auth = FirebaseAuth.getInstance()

        userList = ArrayList()
        userIds = ArrayList()
        val userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        userListView.adapter = userAdapter

        // Initialize chat-related views
        chatListView = findViewById(R.id.chatListView)  //  Ensure these IDs exist in activity_user_list.xml
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        chatMessages = ArrayList()
        chatAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chatMessages)
        chatListView.adapter = chatAdapter

        // Hardcoded user IDs.  Replace with actual user IDs from your database.
        currentUserId = "HBboosN9SkYZdoybAEaKv8Hc2IG2"
        otherUserId = "jKnWV2h7agdQi8y1yujQVPLFowF3"

        // Get the Chat ID
        val chatId = if (currentUserId > otherUserId) {
            "$currentUserId-$otherUserId"
        } else {
            "$otherUserId-$currentUserId"
        }

        // Database listener for chat messages
        FirebaseDatabase.getInstance().getReference("messages").child(chatId).addChildEventListener(object : ChildEventListener { // Corrected Line
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.child("message").getValue(String::class.java)
                val senderId = snapshot.child("sender").getValue(String::class.java)
                if (message != null && senderId != null) {
                    val displayMessage = if (senderId == currentUserId) {
                        "You: $message"
                    } else {
                        "Other: $message"
                    }
                    chatMessages.add(displayMessage)
                    chatAdapter.notifyDataSetChanged()
                    chatListView.setSelection(chatMessages.size - 1)
                    Log.d("UserListActivity", "Message added: $displayMessage")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("UserListActivity", "Message changed")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("UserListActivity", "Message removed")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("UserListActivity", "Message moved")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserListActivity", "Database error: ${error.message}")
                Toast.makeText(this@UserListActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Send button listener
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                val messageData = HashMap<String, String>()
                messageData["sender"] = currentUserId
                messageData["message"] = message

                FirebaseDatabase.getInstance().getReference("messages").child(chatId).push().setValue(messageData) // corrected line
                    .addOnSuccessListener {
                        messageEditText.text.clear()
                        Log.d("UserListActivity", "Message sent: $message")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("UserListActivity", "Failed to send message: ${exception.message}")
                        Toast.makeText(this@UserListActivity, "Failed to send message.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this@UserListActivity, "Enter a message.", Toast.LENGTH_SHORT).show()
            }
        }

        // Load the user list (you might remove this or adapt it)
        database.addListenerForSingleValueEvent(object : ValueEventListener {  // Changed from addValueEventListener
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.w("UserListActivity", "No users found in database.")
                    Toast.makeText(this@UserListActivity, "No users found.", Toast.LENGTH_SHORT).show()
                    return
                }

                database.addValueEventListener(object : ValueEventListener { //  added back
                    override fun onDataChange(snapshot: DataSnapshot) { // Added back
                        userList.clear()
                        userIds.clear()
                        for (childSnapshot in snapshot.children) {
                            val email = childSnapshot.child("email").getValue(String::class.java)
                            val uid = childSnapshot.key
                            if (email != null && uid != null && uid != auth.currentUser?.uid) {
                                userList.add(email)
                                userIds.add(uid)
                                Log.d("UserListActivity", "User found: Email=$email, UID=$uid")
                            }
                        }
                        userAdapter.notifyDataSetChanged()
                        Log.d("UserListActivity", "User list size: ${userList.size}")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("UserListActivity", "Database error: ${error.message}")
                        Toast.makeText(this@UserListActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserListActivity", "Initial database check error: ${error.message}")
                Toast.makeText(this@UserListActivity, "Database check error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        userListView.setOnItemClickListener { _, _, position, _ ->  //  Keep for now.
            //  startChat(userIds[position]) // Removed.  Chat is in this activity now.
        }
    }
}
