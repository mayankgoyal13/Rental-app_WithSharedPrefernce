package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.example.rental_app.databinding.ActivityRegistrationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class RegistrationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegistrationBinding
    private var userList:MutableList<User> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.setupHyperLink()
        this.binding.btnSignUp.setOnClickListener(this)

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()
    }

    private fun setupHyperLink() {
        binding.cbPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnSignUp -> {
                this.signUp()
            }
        }
    }

    private fun signUp() {
        val inputValidation = this.validateInputs()
        if (inputValidation) {
            val firstName = this.binding.etFirstName.text.toString()
            val lastName = this.binding.etLastName.text.toString()
            val email = this.binding.etEmail.text.toString()
            val phoneNumber = this.binding.etPhone.text.toString()
            val password = this.binding.etPassword.text.toString()

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("mailto: $email")
                putExtra(Intent.EXTRA_EMAIL, email)
                putExtra(Intent.EXTRA_SUBJECT, "Welcome to 4Rent.ca")
                putExtra(Intent.EXTRA_TEXT, "Welcome to 4Rent.ca, $firstName. Your account has been created successfully")
            }
            if (emailIntent.resolveActivity(packageManager) != null){
                startActivity(emailIntent)
            }

            val gson = Gson()
            val user = User(firstName, lastName, email, phoneNumber, password, true, mutableListOf())
            userList.add(user)

            val userJson = gson.toJson(user)
            val jsonUserList = gson.toJson(userList)

            prefEditor.putString("USER_LIST", jsonUserList)
            prefEditor.putString("LOGGED_IN_USER", userJson)
            prefEditor.apply()

            Snackbar.make(binding.rootLayout, "Registration Successful", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = this.binding.etFirstName.text.toString()
        val lastName = this.binding.etLastName.text.toString()
        val email = this.binding.etEmail.text.toString()
        val phoneNumber = this.binding.etPhone.text.toString()
        val password = this.binding.etPassword.text.toString()
        val confirmPassword = this.binding.etConfirmPassword.text.toString()
        val privacyPolicyChecked = this.binding.cbPrivacyPolicy.isChecked

        if(firstName.isNullOrEmpty()) {
            binding.etFirstName.error = "First Name is required"
            return false
        } else if(lastName.isNullOrEmpty()) {
            binding.etLastName.error = "Last Name is required"
            return false
        } else if(email.isNullOrEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }else if(phoneNumber.isNullOrEmpty()) {
            binding.etEmail.error = "Phone is required"
            return false
        } else if(password.isNullOrEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        } else if(confirmPassword.isNullOrEmpty()) {
            binding.etConfirmPassword.error = "Password confirmation is required"
            return false
        } else if(password != confirmPassword) {
            binding.tvError.error = "Passwords don't match"
            return false
        } else if(!privacyPolicyChecked) {
            binding.tvError.error = "You must agree to Privacy Policy."
            return false
        } else {
            return true
        }
    }
}