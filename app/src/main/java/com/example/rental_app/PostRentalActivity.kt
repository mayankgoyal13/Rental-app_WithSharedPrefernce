package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.rental_app.databinding.ActivityPostRentalBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostRentalActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPostRentalBinding
    private var listingsList = mutableListOf<Listings>()
    private lateinit var propertyTypeSelected: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostRentalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        val userFromSp = sharedPreferences.getString("LOGGED_IN_USER", null)
        if(userFromSp == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val propertyTypes = mutableListOf<String>("Property Type","Apartment", "Basement", "Condo", "House", "Multi Family House", "Townhouse")

        val spinner: Spinner = findViewById(R.id.etPropertyType)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, propertyTypes)

        spinner.adapter = arrayAdapter
        this.binding.etPropertyType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position > 0) {
                    propertyTypeSelected = propertyTypes.get(position)
                } else {
                    Snackbar.make(binding.root, "Please select property type", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.btnPostRental.setOnClickListener(this)
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

    private fun validateInputs(): Boolean {
        val bedrooms = binding.etBedrooms.text.toString()
        val kitchen = binding.etKitchen.text.toString()
        val bathrooms = binding.etBathrooms.text.toString()
        val description = binding.etDescription.text.toString()
        val address = binding.etAddress.text.toString()
        val buildingName = binding.etBuildingName.text.toString()
        val postalCode = binding.etPostalCode.text.toString()
        val province = binding.etProvince.text.toString()
        val city = binding.etCity.text.toString()
        val rent = binding.etRent.text.toString()

        if(bedrooms.isNullOrEmpty()) {
            binding.etBedrooms.error = "This field is required"
            return false
        } else if(kitchen.isNullOrEmpty()) {
            binding.etKitchen.error = "This field is required"
            return false
        } else if(bathrooms.isNullOrEmpty()) {
            binding.etBathrooms.error = "This field is required"
            return false
        } else if(description.isNullOrEmpty()) {
            binding.etDescription.error = "This field is required"
            return false
        } else if(address.isNullOrEmpty()) {
            binding.etAddress.error = "This field is required"
            return false
        } else if(buildingName.isNullOrEmpty()) {
            binding.etBuildingName.error = "This field is required"
            return false
        } else if(postalCode.isNullOrEmpty()) {
            binding.etPostalCode.error = "This field is required"
            return false
        } else if(province.isNullOrEmpty()) {
            binding.etProvince.error = "This field is required"
            return false
        } else if(city.isNullOrEmpty()) {
            binding.etCity.error = "This field is required"
            return false
        } else if(rent.isNullOrEmpty()) {
            binding.etRent.error = "This field is required"
            return false
        } else {
            return true
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnPostRental -> {
                this.postRental()
            }
        }
    }

    private fun postRental() {
        val inputValidation = this.validateInputs()
        if(inputValidation) {
            val bedrooms = binding.etBedrooms.text.toString().toInt()
            val kitchen = binding.etKitchen.text.toString().toInt()
            val bathrooms = binding.etBathrooms.text.toString().toDouble()
            val description = binding.etDescription.text.toString()
            val address = binding.etAddress.text.toString()
            val isAvailable = binding.swAvailable.isChecked
            val buildingName = binding.etBuildingName.text.toString()
            val postalCode = binding.etPostalCode.text.toString()
            val province = binding.etProvince.text.toString()
            val city = binding.etCity.text.toString()
            val rent = binding.etRent.text.toString().toInt()
            val img  = binding.etImage.text.toString()

            val loggedInUser = sharedPreferences.getString("LOGGED_IN_USER", null)
            if(loggedInUser != null) {
                val gson = Gson()
                val userFromSp = sharedPreferences.getString("LOGGED_IN_USER", null)
                val userObj = gson.fromJson(userFromSp, User::class.java)
                val listing = Listings(userObj, propertyTypeSelected, bedrooms, kitchen, bathrooms, description, address, isAvailable, buildingName, postalCode, province, city, img, rent)
                val listingsListFromSP = sharedPreferences.getString("KEY_LISTINGS_LIST", "")

                if (listingsListFromSP != "") {
                    val gson = Gson()
                    val typeToken = object : TypeToken<List<Listings>>() {}.type
                    listingsList = gson.fromJson<MutableList<Listings>>(listingsListFromSP, typeToken)
                    listingsList.add(listing)
                    val listAsString = gson.toJson(listingsList)
                    this.prefEditor.putString("KEY_LISTINGS_LIST", listAsString)
                    this.prefEditor.apply()
                }

                val myListingIntent = Intent(this, MyListingsActivity::class.java)
                startActivity(myListingIntent)
                binding.etBedrooms.text.clear()
                binding.etKitchen.text.clear()
                binding.etBathrooms.text.clear()
                binding.etDescription.text.clear()
                binding.etAddress.text.clear()
                binding.swAvailable.isChecked = false
                binding.etBuildingName.text.clear()
                binding.etPostalCode.text.clear()
                binding.etProvince.text.clear()
                binding.etCity.text.clear()
                binding.etRent.text.clear()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}