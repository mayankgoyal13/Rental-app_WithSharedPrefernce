package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rental_app.databinding.ActivityMyShortlistingsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyShortlistingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMyShortlistingsBinding
    private lateinit var adapter: MyShortlistingsAdapter
    private lateinit var prefEditor: SharedPreferences.Editor

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyShortlistingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        initializeListing()

        val loggedInUserJson = sharedPreferences.getString("LOGGED_IN_USER", null)
        val typeUser = object : TypeToken<User>() {}.type
        val loggedInUser: User = gson.fromJson(loggedInUserJson, typeUser) ?: return

        adapter = MyShortlistingsAdapter(loggedInUser.shortListing) { listingToRemove ->
            removeListingAndUpdate(loggedInUser, listingToRemove)
        }

        binding.rv.apply {
            layoutManager = LinearLayoutManager(this@MyShortlistingsActivity)
            adapter = this@MyShortlistingsActivity.adapter
            addItemDecoration(DividerItemDecoration(this@MyShortlistingsActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun removeListingAndUpdate(user: User, listing: Listings) {
        // Remove the listing from the logged-in user's shortlist
        user.shortListing.remove(listing)

        // Update the user in the USER_LIST
        val usersJson = sharedPreferences.getString("USER_LIST", null)
        val typeUsersList = object : TypeToken<List<User>>() {}.type
        val users: MutableList<User> = gson.fromJson(usersJson, typeUsersList) ?: mutableListOf()

        val userIndex = users.indexOfFirst { it.email == user.email }
        if (userIndex != -1) {
            users[userIndex].shortListing.remove(listing)
        }

        val updatedLoggedInUserJson = gson.toJson(user)
        sharedPreferences.edit().putString("LOGGED_IN_USER", updatedLoggedInUserJson).apply()
        val updatedUsersJson = gson.toJson(users)
        sharedPreferences.edit().putString("USER_LIST", updatedUsersJson).apply()

        // Notify the adapter of the data change
        adapter.notifyDataSetChanged()
    }


    private fun initializeListing() {
        val loggedInUserJson = sharedPreferences.getString("LOGGED_IN_USER", null)
        val typeUser = object : TypeToken<User>() {}.type
        val loggedInUser: User = gson.fromJson(loggedInUserJson, typeUser) ?: return
        val sampleUser = User("John", "Doe", "johndoe@email.com", "1234567890", "password", false)

        // Check if the user already has listings
        if (loggedInUser.shortListing.isEmpty()) {
            listOf(
                Listings(sampleUser, "Apartment", 2, 1, 1.0, "Cozy two-bedroom apartment", "123 Main St", true, "Main Towers", "12345", "New York", "New York", "img5", 1200),
                Listings(sampleUser, "House", 3, 1, 2.0, "Spacious house with garden", "456 Elm St", true, "Elm Residence", "23456", "Los Angeles", "California", "img3", 2500),
                Listings(sampleUser, "Townhouse", 3, 2, 2.5, "Modern townhouse with rooftop patio", "456 Park Ave", false, "Parkside Complex", "67890", "Chicago", "Illinois", "img4", 1800)
            ).forEach { listing ->
                loggedInUser.shortListing.add(listing)
            }

            // Save the updated user back to SharedPreferences
            val editor = sharedPreferences.edit()
            val updatedUserJson = gson.toJson(loggedInUser)
            editor.putString("LOGGED_IN_USER", updatedUserJson)
            editor.apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.menuMyListings -> {
                val myListingIntent = Intent(this, MyListingsActivity::class.java)
                startActivity(myListingIntent)
                return true
            }
            R.id.menuPostRental -> {
                val postRentalIntent = Intent(this, PostRentalActivity::class.java)
                startActivity(postRentalIntent)
                return true
            }
            R.id.menuLogout -> {
                prefEditor.remove("LOGGED_IN_USER")
                startActivity(Intent(this, LoginActivity::class.java))

                return  true
            }
            R.id.menuHome -> {
                startActivity(Intent(this, MainActivity::class.java))
                return  true
            }
            R.id.menuShortList -> {
                startActivity(Intent(this, MyShortlistingsActivity::class.java))
                return  true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}