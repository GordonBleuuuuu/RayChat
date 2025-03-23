package com.pedrenareyes.raychat

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pedrenareyes.raychat.R

class UserListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userListView: ListView
    private lateinit var signOutButton: Button
    private lateinit var createNewMessageButton: Button
    private lateinit var profileButton: Button
    private lateinit var userList: ArrayList<String>
    private lateinit var userIds: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        Log.d("UserListActivity", "onCreate called") // Log activity creation

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        userListView = findViewById(R.id.userListView)
        signOutButton = findViewById(R.id.signOutButton)
        createNewMessageButton = findViewById(R.id.createNewMessageButton)
        profileButton = findViewById(R.id.profileButton)

        userList = ArrayList()
        userIds = ArrayList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        userListView.adapter = adapter

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
                        Log.d("UserListActivity", "User found: Email=$email, UID=$uid") // Log user found
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserListActivity", "Database error: ${error.message}") // Log database error
            }
        })

        userListView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", userIds[position])
            startActivity(intent)
            Log.d("UserListActivity", "User selected: UID=${userIds[position]}") // Log user selection
        }

        signOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
            Log.d("UserListActivity", "Sign out button pressed") // Log sign out
        }

        createNewMessageButton.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            startActivity(intent)
            Log.d("UserListActivity", "Create new message button pressed") // Log new message
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            Log.d("UserListActivity", "Profile button pressed") // Log profile button
        }
    }
}