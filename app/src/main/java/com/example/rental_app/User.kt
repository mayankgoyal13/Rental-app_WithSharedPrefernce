package com.example.rental_app

class User(
    var firstName: String,
    var lastName: String,
    var email: String,
    var phoneNumber: String,
    var password: String,
    var isLoggedIn: Boolean,
    var shortListing: MutableList<Listings> = mutableListOf()
) {

    fun addListing(listing: Listings) {
        shortListing.add(listing)
    }


    fun removeListing(listing: Listings) {
        shortListing.remove(listing)
    }

    override fun toString(): String {
        return "User(firstName='$firstName', lastName='$lastName', email='$email', phoneNumber='$phoneNumber', password='$password', isLoggedIn=$isLoggedIn, listings=$shortListing)"
    }
}