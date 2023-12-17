package com.example.rental_app


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CitiesAdaptor(
    private val cities:List<String>,
    private val buttonClickHandler:(Int) -> Unit):RecyclerView.Adapter<CitiesAdaptor.CitiesViewHolder>() {

    inner class CitiesViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {
            itemView.findViewById<Button>(R.id.city).setOnClickListener{
                buttonClickHandler(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.layout_cities, parent, false)
        return CitiesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {

        val tv = holder.itemView.findViewById<TextView>(R.id.city)
        tv.text = "${cities.get(position)}"
    }
}