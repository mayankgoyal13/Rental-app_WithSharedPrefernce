package com.example.rental_app

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rental_app.databinding.ActivityMyListingsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyListingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListingsBinding
    private lateinit var adapter: MyListingAdapter
    private var listingsList = listOf<Listings>()
    private var myListings: List<Listings> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val gson = Gson()
                val myListingsSp = sharedPreferences.getString("MY_LISTINGS", "")
                val typeToken = object : TypeToken<List<Listings>>() {}.type
                this.myListings = gson.fromJson<List<Listings>>(myListingsSp, typeToken)

                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        val gson = Gson()
        val listingsListFromSP = sharedPreferences.getString("KEY_LISTINGS_LIST", "")

        val userFromSp = sharedPreferences.getString("LOGGED_IN_USER", "")
        val userObj = gson.fromJson(userFromSp, User::class.java)

        if (listingsListFromSP != "") {
            val typeToken = object : TypeToken<List<Listings>>() {}.type
            listingsList = gson.fromJson<List<Listings>>(listingsListFromSP, typeToken)
            myListings = listingsList.filter { it.owner.email == userObj.email }
            val myListAsString = gson.toJson(myListings)
            this.prefEditor.putString("MY_LISTINGS", myListAsString)
            this.prefEditor.apply()
        }

        this.adapter = MyListingAdapter(myListings, {pos -> rowClicked(pos)}, {pos -> editClickHandler(pos)})
        binding.rv.adapter = adapter

        binding.rv.layoutManager = LinearLayoutManager(this)

        binding.rv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
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

    fun rowClicked(position:Int){
//        intent to show full page for listing page
        val gson = Gson()
        val objectAsString = gson.toJson(myListings.get(position))
        val descriptionIntent = Intent(this, ResultDescriptionActivity::class.java)
        descriptionIntent.putExtra("CLICKED_LISTING",objectAsString)
        descriptionIntent.putExtra("SOURCE", "MyListing")
        startActivity(descriptionIntent)
    }

    private fun editClickHandler(position: Int) {
        val editIntent = Intent(this, EditListingActivity::class.java)
        editIntent.putExtra("LISTING_POSITION", position)
        startForResult.launch(editIntent)
    }

    override fun onResume() {
        super.onResume()
        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        val gson = Gson()

            val typeToken = object : TypeToken<List<Listings>>() {}.type
            val listingsListFromSp = sharedPreferences.getString("MY_LISTINGS", "")
        Log.d("listingsListFromSp", "onResume: $listingsListFromSp")
            if(listingsListFromSp != "") {
                val updatedMyListings = gson.fromJson<List<Listings>>(listingsListFromSp, typeToken)
                myListings = updatedMyListings
                val myListAsString = gson.toJson(myListings)
                this.prefEditor.putString("MY_LISTINGS", myListAsString)
                this.prefEditor.apply()
            }

        this.adapter = MyListingAdapter(myListings, {pos -> rowClicked(pos)}, {pos -> editClickHandler(pos)})
        binding.rv.adapter = adapter

        binding.rv.layoutManager = LinearLayoutManager(this)

        binding.rv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        adapter.notifyDataSetChanged()
    }

}