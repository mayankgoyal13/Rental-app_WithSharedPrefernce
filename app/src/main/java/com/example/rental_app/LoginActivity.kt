package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rental_app.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
//        clearPreferences()
        initializeUsers()

        binding.loginButton.setOnClickListener {
            val email = binding.username.text.toString() // Assuming the username field is for email
            val password = binding.password.text.toString()
            val loggedInUser = isValidLogin(email, password)

            if (loggedInUser != null) {
                binding.errorMessage.text = "VALID User" // Update the message for successful login

                // Store the logged-in user's information in SharedPreferences
                val editor = sharedPreferences.edit()
                val gson = Gson()
                val json = gson.toJson(loggedInUser)
                editor.putString("LOGGED_IN_USER", json)
                editor.apply()

                // Proceed to open the ProfileActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                Log.d("LoginActivity", "Login successful for user: $email")
            } else {
                binding.errorMessage.text = "Invalid username or password"
                Log.d("LoginActivity", "Login failed for user: $email")
            }
        }
    }

    private fun initializeUsers() {
        val usersExist = sharedPreferences.contains("USER_LIST")

        if (!usersExist) {
            val users = listOf(
                User("Hesus", "Garcia", "hesus@mail.com", "1234567890", "password1", false, mutableListOf()),
                User("Mayank", "Goyal", "mayan@mail.com", "0987654321", "password2", false, mutableListOf()),
                User("Dwij", "Virani", "dwij@mail.com", "1122334455", "password3", false, mutableListOf())
            )
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(users)
            editor.putString("USER_LIST", json)
            editor.apply()

            Log.d("LoginActivity", "initializeUsers: Users initialized")
        } else {
            Log.d("LoginActivity", "initializeUsers: Users already exist")
        }
    }

    private fun isValidLogin(email: String, password: String): User? {
        val gson = Gson()
        val json = sharedPreferences.getString("USER_LIST", null)
        val typeToken = object : TypeToken<List<User>>() {}.type
        var users: MutableList<User> = gson.fromJson(json, typeToken) ?: mutableListOf()

        val foundUser = users.find { it.email == email && it.password == password }
        if (foundUser != null) {
            // Set isLoggedIn to true for the found user
            val index = users.indexOf(foundUser)
            users[index].isLoggedIn = true

            // Save the updated users list back to SharedPreferences
            val editor = sharedPreferences.edit()
            val updatedJson = gson.toJson(users)
            editor.putString("USER_LIST", updatedJson)
            editor.apply()

            return foundUser
        }

        return null
    }

    private fun clearPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        Log.d("LoginActivity", "clearPreferences: SharedPreferences cleared")
    }

}
