package com.example.rental_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class MyShortlistingsAdapter(
    private var myListings: MutableList<Listings>,
    private val onItemRemoved: (Listings) -> Unit
) : RecyclerView.Adapter<MyShortlistingsAdapter.MyShortlistingsViewHolder>() {
    private val gson = Gson()

    inner class MyShortlistingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPropertyType: TextView = itemView.findViewById(R.id.tvPropertyType)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvRent: TextView = itemView.findViewById(R.id.tvRent)
        val imageViewListing: ImageView = itemView.findViewById(R.id.imageViewListing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyShortlistingsViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout_my_shortlistings, parent, false)
        return MyShortlistingsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myListings.size
    }

    override fun onBindViewHolder(holder: MyShortlistingsViewHolder, position: Int) {
        val listing = myListings[position]
        holder.tvPropertyType.text = listing.propertType
        holder.tvDescription.text = listing.description
        holder.tvAddress.text = listing.address
        holder.tvRent.text = "Rent: ${listing.rent}"
        // 2c. Populate the image
        // - getting a context variable
        val context = holder.itemView.context

        // - use the context to update the image
        // Assuming listing.img is a String with the filename of the drawable
        val resId = context.resources.getIdentifier(listing.img, "drawable", context.packageName)

        // Set the image resource to the ImageView
        holder.imageViewListing.setImageResource(resId)

        holder.itemView.findViewById<Button>(R.id.btnRemoveListing).setOnClickListener {
            // Remove the item and notify the adapter
            myListings.removeAt(position)
            notifyItemRemoved(position)
            // Call the passed function to handle additional logic
            onItemRemoved(listing)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("LISTING_DETAILS", gson.toJson(listing))
            }
            context.startActivity(intent)
        }

    }
}
