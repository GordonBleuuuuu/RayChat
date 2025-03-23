package com.pedrenareyes.raychat

import android.content.Intent
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

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        Log.d("LoginFragment", "onCreateView called") // Log fragment creation

        auth = FirebaseAuth.getInstance()
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            Log.d("LoginFragment", "Login attempt: Email=$email, Password=$password") // Log user input

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Log.d("LoginFragment", "Login successful, UID: ${user?.uid}") // Log successful login
                        Toast.makeText(context, "Login successful.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), UserListActivity::class.java)
                        startActivity(intent)
                        activity?.finish() //finish the auth activity
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("LoginFragment", "Login failed: ${task.exception?.message}") // Log login failure
                        Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}