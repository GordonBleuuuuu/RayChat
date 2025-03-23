package com.pedrenareyes.raychat

import android.os.Bundle
import android.util.Log // Import Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.pedrenareyes.raychat.R
import com.google.firebase.database.FirebaseDatabase

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        Log.d("SignupFragment", "onCreateView called") // Log fragment creation

        auth = FirebaseAuth.getInstance()
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        signupButton = view.findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            Log.d("SignupFragment", "Signup attempt: Email=$email, Password=$password") // Log user input

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign up success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        val database = FirebaseDatabase.getInstance()
                        val usersRef = database.getReference("users")

                        user?.uid?.let { uid ->
                            usersRef.child(uid).child("email").setValue(email)
                            Log.d("SignupFragment", "Signup successful, UID: $uid") // Log successful signup
                        }

                        Toast.makeText(context, "Sign up successful.", Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.e("SignupFragment", "Signup failed: ${task.exception?.message}") // Log signup failure
                        Toast.makeText(context, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}