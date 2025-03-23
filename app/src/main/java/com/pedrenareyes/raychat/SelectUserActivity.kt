package com.pedrenareyes.raychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton // Import ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SelectUserActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userListView: ListView
    private lateinit var userList: ArrayList<String>
    private lateinit var userIds: ArrayList<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var addButton: ImageButton // Add ImageButton declaration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        Log.d("SelectUserActivity", "onCreate called")

        database = FirebaseDatabase.getInstance().getReference("users")
        userListView = findViewById(R.id.selectUserListView)
        auth = FirebaseAuth.getInstance()
        addButton = findViewById(R.id.addButton) // Initialize ImageButton

        userList = ArrayList()
        userIds = ArrayList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        userListView.adapter = adapter

        // Add a single value listener to check if the database has data
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.w("SelectUserActivity", "No users found in database.")
                    Toast.makeText(this@SelectUserActivity, "No users found.", Toast.LENGTH_SHORT).show()
                    return // Exit if no users
                }

                // If data exists, proceed with the value event listener
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()
                        userIds.clear()
                        for (childSnapshot in snapshot.children) {
                            val email = childSnapshot.child("email").getValue(String::class.java)
                            val uid = childSnapshot.key
                            if (email != null && uid != null && uid != auth.currentUser?.uid) {
                                userList.add(email)
                                userIds.add(uid)
                                Log.d("SelectUserActivity", "User found: Email=$email, UID=$uid")
                            }
                        }
                        adapter.notifyDataSetChanged()
                        Log.d("SelectUserActivity", "User list size: ${userList.size}")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("SelectUserActivity", "Database error: ${error.message}")
                        Toast.makeText(this@SelectUserActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SelectUserActivity", "Initial database check error: ${error.message}")
                Toast.makeText(this@SelectUserActivity, "Database check error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        userListView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@SelectUserActivity, ChatActivity::class.java)
            intent.putExtra("userId", userIds[position])
            startActivity(intent)
            Log.d("SelectUserActivity", "User selected: UID=${userIds[position]}")
        }

        addButton.setOnClickListener {
            // Start the NewMessageActivity
            val intent = Intent(this@SelectUserActivity, NewMessageActivity::class.java)
            startActivity(intent)
            Log.d("SelectUserActivity", "Add button clicked, starting NewMessageActivity")
        }
    }
}