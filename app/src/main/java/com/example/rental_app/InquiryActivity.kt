package com.example.rental_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.rental_app.databinding.ActivityInquiryBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class InquiryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityInquiryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        if(intent != null) {
            val gson = Gson()
            val dataFromIntent = intent.getStringExtra("LISTING_DATA")
            val dataObj = gson.fromJson(dataFromIntent, Listings::class.java)
            Log.d("dataObj", "onCreate: $dataObj")

            binding.etFirstName.setText(dataObj.owner.firstName)
            binding.etLastName.setText(dataObj.owner.lastName)
            binding.etEmail.setText(dataObj.owner.email)
            binding.etPhone.setText(dataObj.owner.phoneNumber)

        }

        binding.btnSendInquiry.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btnSendInquiry -> {
                this.sendInquiry()
            }
        }
    }

    private fun sendInquiry() {
//        val emailIntent = Intent(Intent.ACTION_SEND).apply {
//            val gson = Gson()
//            val dataFromIntent = intent.getStringExtra("LISTING_DATA")
//            val dataObj = gson.fromJson(dataFromIntent, Listings::class.java)
//
//            data = Uri.parse("mailto: ${dataObj.owner.email}")
//            putExtra(Intent.EXTRA_EMAIL, dataObj.owner.email)
//            putExtra(Intent.EXTRA_SUBJECT, "You have new inquiry for your listing")
//            putExtra(Intent.EXTRA_TEXT, binding.etMessage.text.toString())
//
//        }
//        if (emailIntent.resolveActivity(packageManager) != null){
//            startActivity(emailIntent)
//        }
        val gson = Gson()
        val dataFromIntent = intent.getStringExtra("LISTING_DATA")
        val dataObj = gson.fromJson(dataFromIntent, Listings::class.java)

        val it = Intent(Intent.ACTION_SEND)
        it.putExtra(Intent.EXTRA_EMAIL, arrayOf("dwijvirani23@gmail.com"))
        it.putExtra(Intent.EXTRA_SUBJECT, "You have new inquiry for your listing")
        it.putExtra(Intent.EXTRA_TEXT, binding.etMessage.text.toString())
        it.type = "message/rfc822"

        Snackbar.make(binding.rootLayout, "Inquiry Sent", Snackbar.LENGTH_SHORT).show()
        Handler().postDelayed({
            finish()
        }, 2000)
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