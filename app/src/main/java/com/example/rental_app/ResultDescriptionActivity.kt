package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import com.example.rental_app.databinding.ActivityResultDescriptionBinding
import com.example.rental_app.databinding.ActivityResultsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultDescriptionActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityResultDescriptionBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var loggedUserObject:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Mayank", "hereeeeeeeee")
        this.binding = ActivityResultDescriptionBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        var sourceScreen = intent.getStringExtra("SOURCE")
        if(sourceScreen == "MyListing") {
            binding.callOwner.isVisible = false
            binding.enquiry.isVisible = false
        }

        val loggedUser = sharedPreferences.getString("LOGGED_IN_USER", "")
        if (loggedUser != "") {
            val gson = Gson()
            // define what type of data we should convert the string back to
            val typeToken = object : TypeToken<User>() {}.type
            // convert the string back to a object
            loggedUserObject = gson.fromJson<User>(loggedUser, typeToken)

        }


//        var receivedData = intent.getSerializableExtra("CLICKED_LISTING") as Listings
        val gson = Gson()
        var receivedData = intent.getStringExtra("CLICKED_LISTING")
        val dataAsObject:Listings = gson.fromJson(receivedData, Listings ::class.java)
        val res = this.resources.getIdentifier(dataAsObject.img, "drawable", this.packageName)
        binding.listingImg.setImageResource(res)
        binding.rent.text = "$${dataAsObject.rent}/-"
        binding.desc.text = dataAsObject.description
        binding.address.text = dataAsObject.address
        binding.specs.text = "${dataAsObject.bedrooms}Bed|${dataAsObject.bathroom}Bath|\n${dataAsObject.kitchen}Kitchen"
        binding.cityProvince.text = "${dataAsObject.city},${dataAsObject.province}"
        if (dataAsObject.isAvailable){
            binding.status.text = "AVAILABLE"
        }else{
            binding.status.text = "UNAVAILABLE"
            binding.status.setTextColor(Color.parseColor("#C50707"))
        }

//        binding.shortlist.setOnClickListener {
//            Log.d("shortlist", "${loggedUserObject.shortListing}")
//            Log.d("loggedUserObject", "onCreate: $loggedUserObject")
//            if(loggedUserObject != null) {
//                Log.d("shortlist2", "************************************************************${loggedUserObject.shortListing.contains(dataAsObject)}")
//                if (!loggedUserObject.shortListing.contains(dataAsObject)){
//                    loggedUserObject.shortListing.add(dataAsObject)
//                    Log.d("shortlist3", "************************************************************")
//                    val gson = Gson()
//                    val updatedLoggedUserAsString = gson.toJson(loggedUserObject)
//                    this.prefEditor.putString("LOGGED_IN_USER", updatedLoggedUserAsString)
//                    this.prefEditor.apply()
//                    Log.d("shortlist4", "************************************************************")
//                }
//            } else {
//                startActivity(Intent(this, LoginActivity::class.java))
//            }
//        }


        binding.shortlist.setOnClickListener {
            Log.d("shortlist", "${loggedUserObject.shortListing}")
            if(loggedUserObject.shortListing == null) {
                loggedUserObject.shortListing = mutableListOf()
                val gson = Gson()
                val updatedLoggedUserAsString = gson.toJson(loggedUserObject)
                this.prefEditor.putString("LOGGED_IN_USER", updatedLoggedUserAsString)
                this.prefEditor.apply()
            }

            this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
            prefEditor = sharedPreferences.edit()
            if (!loggedUserObject.shortListing.contains(dataAsObject)){
                loggedUserObject.shortListing.add(dataAsObject)
                val gson = Gson()
                val updatedLoggedUserAsString = gson.toJson(loggedUserObject)
                this.prefEditor.putString("LOGGED_IN_USER", updatedLoggedUserAsString)
                this.prefEditor.apply()

                finish()
            }
        }

        binding.callOwner.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${dataAsObject.owner.phoneNumber}")
            }

            if (callIntent.resolveActivity(packageManager) != null){
                startActivity(callIntent)
            }
        }

        binding.enquiry.setOnClickListener {
            val gson = Gson()
            val inquiryIntent = Intent(this, InquiryActivity::class.java)
            inquiryIntent.putExtra("LISTING_DATA", gson.toJson(dataAsObject))
            startActivity(inquiryIntent)
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