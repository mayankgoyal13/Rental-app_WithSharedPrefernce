package com.example.rental_app

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.rental_app.databinding.ActivityEditListingBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditListingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditListingBinding
    private var rowPosition:Int = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    private lateinit var propertyTypeSelected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

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

        if (intent != null) {
            this.rowPosition = intent.getIntExtra("LISTING_POSITION", -1)

            val gson = Gson()
            val listingsListFromSP = sharedPreferences.getString("MY_LISTINGS", "")
            val typeToken = object : TypeToken<List<Listings>>() {}.type
            val listingsList = gson.fromJson<List<Listings>>(listingsListFromSP, typeToken)
            val originalData = listingsList[rowPosition]

            val spinnerPosition = arrayAdapter.getPosition(originalData.propertType)
            spinner.setSelection(spinnerPosition)
            binding.etAddress.setText(originalData.address)
            binding.etBuildingName.setText(originalData.buildingName)
            binding.etProvince.setText(originalData.province)
            binding.etCity.setText(originalData.city)
            binding.etPostalCode.setText(originalData.postalCode)
            binding.etBedrooms.setText(originalData.bedrooms.toString())
            binding.etKitchen.setText(originalData.kitchen.toString())
            binding.etBathrooms.setText(originalData.bathroom.toString())
            binding.etDescription.setText(originalData.description)
            binding.etRent.setText(originalData.rent.toString())
            binding.etImage.setText(originalData.img)
        }

        binding.btnEditRental.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btnEditRental -> {
                this.editRental()
            }
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

    private fun editRental() {
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
            val img = binding.etImage.text.toString()

            val loggedInUser = sharedPreferences.getString("LOGGED_IN_USER", null)
            if(loggedInUser != null) {
                val gson = Gson()
                val listingsListFromSP = sharedPreferences.getString("MY_LISTINGS", "")
                val typeToken = object : TypeToken<List<Listings>>() {}.type
                val myListings = gson.fromJson<MutableList<Listings>>(listingsListFromSP, typeToken)
                val userFromSp = sharedPreferences.getString("LOGGED_IN_USER", null)
                val userObj = gson.fromJson(userFromSp, User::class.java)
                val listing = Listings(userObj, propertyTypeSelected, bedrooms, kitchen, bathrooms, description, address, isAvailable, buildingName, postalCode, province, city, img, rent)

                myListings.removeAt(rowPosition)
                myListings.add(rowPosition, listing)

                val myListAsString = gson.toJson(myListings)
                this.prefEditor.putString("MY_LISTINGS", myListAsString)
                this.prefEditor.apply()

                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
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