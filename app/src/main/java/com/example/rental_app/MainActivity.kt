package com.example.rental_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import android.content.SharedPreferences
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.rental_app.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.util.Objects
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
//    val listing1 = Listings("test@gmail.com",1230,"40 Fountainhead Road","New Renovated modern interior 2 bed 2 bath apartment","Toronto","Ontario",9714458889,false,"Mayank")
//    val listng2 = Listings("test2@gmail.com",1190,"1-sentinel rd", "Not provided","Toronto","Ontario",9888111991,false,"Mayank")
//    val listingsList = mutableListOf<Listings>(listing1,listng2)
    val citiesList = listOf<String>("Toronto","Calgary","Ottawa","Regina")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.binding.register.setOnClickListener{registerClickHandler()}
        this.binding.postRental.setOnClickListener { postrentalClickHandler() }
        this.binding.login.setOnClickListener { loginClickHandler() }

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

//        Fillings some Listings to shared preferences
        val user1 = User("Mayank","Goyal","test@gmail.com","0000000000","Password",false, mutableListOf())
        val user2 = User("Mayank","Goyal","test@gmail.com","1000000000","Password",false, mutableListOf())

        val user3 = User("Dwij","Virani","dwij@mail.com","2269989245","password3",false, mutableListOf())
        val listing1 = Listings(user2,"Condo",2,1,2.0,"newly renovated property","test address",true,"fountainhead","l3t2g","ON","Toronto","img3",1200)
        val listing2 = Listings(user1,"Condo",2,1,2.0,"newly renovated property","test address",false,"fountainhead","l3t2gf","ON","Toronto","img4",2200)

        val listing3 = Listings(user3,"House",1,1,1.0,"High rise APt","306 Albion Road",true,"Mountain View","L4KP9V","AB","Calgary","img5",3400)
        val listing4 = Listings(user1,"Townhouse",5,2,3.0,"Good location","122 Parkway Ave",true,"Millway Apt","M8P5C4","AB","Calgary","img4",5000)

//
        val listingsListFromSP = sharedPreferences.getString("KEY_LISTINGS_LIST", "")
        if(listingsListFromSP == "") {
            val listingsList = listOf<Listings>(listing1,listing2,listing3,listing4)
            prefEditor = this.sharedPreferences.edit()
            val gson = Gson()
            val listAsString = gson.toJson(listingsList)
            this.prefEditor.putString("KEY_LISTINGS_LIST", listAsString)
            this.prefEditor.apply()
        }



        binding.searchButton.setOnClickListener {
            if(binding.search.text.toString().isNullOrEmpty()){
                binding.searchError.isVisible = true
                return@setOnClickListener
            }
            SearchResults(binding.search.text.toString())
        }


        var adapter:CitiesAdaptor = CitiesAdaptor(citiesList,
            {pos -> buttonClicked(pos) })
        binding.rvCities.adapter = adapter
        binding.rvCities.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        binding.searchError.isVisible = false
    }
    fun SearchResults(searchval:String){
        val ResultsIntent = Intent(this@MainActivity, ResultsActivity::class.java)
        ResultsIntent.putExtra("SEARCH_VAL",searchval)
        startActivity(ResultsIntent)
    }

    fun buttonClicked(position:Int){
        SearchResults(citiesList.get(position))

    }
    private fun registerClickHandler() {
        val loggedInUser = sharedPreferences.getString("LOGGED_IN_USER", "")
        if(loggedInUser == "") {
            val registerIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(registerIntent)
        } else {
            binding.register.isEnabled = false
        }
    }
    private fun postrentalClickHandler() {
        val loggedInUser = sharedPreferences.getString("LOGGED_IN_USER", "")
        if(loggedInUser != "") {
            val postrentalIntent = Intent(this, PostRentalActivity::class.java)
            startActivity(postrentalIntent)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun loginClickHandler() {
        val loggedInUser = sharedPreferences.getString("LOGGED_IN_USER", "")
        if(loggedInUser == "") {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        } else {
            binding.register.isEnabled = false
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
