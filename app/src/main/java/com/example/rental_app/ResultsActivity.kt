package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rental_app.databinding.ActivityMainBinding
import com.example.rental_app.databinding.ActivityResultsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultsBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    var listingsList = listOf<Listings>()


    var filteredList:List<Listings> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        prefEditor = sharedPreferences.edit()

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val listingsListFromSP = sharedPreferences.getString("KEY_LISTINGS_LIST", "")

        if (listingsListFromSP != "") {
            // convert the string back into a fruit object
            val gson = Gson()
            // define what type of data we should convert the string back to
            val typeToken = object : TypeToken<List<Listings>>() {}.type
            // convert the string back to a list
            listingsList = gson.fromJson<List<Listings>>(listingsListFromSP, typeToken)

        }

//
//        this.sharedPreferences = getSharedPreferences("LISTINGS", MODE_PRIVATE)
//        this.prefEditor = this.sharedPreferences.edit()
//
//        val listingsFromSP = sharedPreferences.getString("ALL_LISTINGS", null)
//        if (listingsFromSP != null) {
//            // convert the string back to a Student object
//            val gson = Gson()
//            val studentObject = gson.fromJson<Listings>(listingsFromSP, Listings::class.java)
//        }

        var receivedData = intent.getStringExtra("SEARCH_VAL").toString()
        filteredList = searchListing(listingsList,receivedData)

        if (receivedData.isNullOrEmpty() || filteredList.isNullOrEmpty()){
            binding.empty.isVisible = true
            Handler().postDelayed({
                finish()
            }, 2000)

        }

        binding.searchedValue.text = "${receivedData.trim().uppercase()} :"


        var adapter:ResultsAdaptor = ResultsAdaptor(
            filteredList,
            {pos -> rowClicked(pos) },
            {pos -> callButtonClicked(pos)},
            {pos -> enquiryButtonClicked(pos)}
        )
        binding.rv.adapter = adapter
        binding.rv.layoutManager = GridLayoutManager(this, 1)


    }

    fun rowClicked(position:Int){
//        intent to show full page for listing page
        val loggedInUser = sharedPreferences.getString("LOGGED_IN_USER", "")
        if(loggedInUser != "") {
            val gson = Gson()
            val objectAsString = gson.toJson(filteredList.get(position))
            val ResultsDescriptionIntent = Intent(this@ResultsActivity, ResultDescriptionActivity::class.java)
            ResultsDescriptionIntent.putExtra("CLICKED_LISTING",objectAsString)
            startActivity(ResultsDescriptionIntent)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    fun callButtonClicked(position: Int){
        val ph_no = filteredList.get(position).owner.phoneNumber
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$ph_no")
        }

        if (callIntent.resolveActivity(packageManager) != null){
            startActivity(callIntent)
        }
    }
    fun enquiryButtonClicked(position: Int){
//        Intent to Enquiry Page
    }

    fun searchListing(list: List<Listings>, searchValue: String):List<Listings>{

        Log.d("mayank", searchValue)
        val result:List<Listings> = list.filter { it.city.lowercase() == searchValue.trim().lowercase() || it.postalCode == searchValue.trim().lowercase() }
        return result
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