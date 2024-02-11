package com.gloory.intellegencegames.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.CardItemViewBinding

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 11.02.2024               │
//└──────────────────────────┘

class HomePageCustomAdapter (private val getList : List<HomePage_ItemViewModel>) :
    RecyclerView.Adapter<HomePageCustomAdapter.ViewHolder>() {

    private var itemClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = getList[position]

        holder.imageView.setImageResource(itemsViewModel.image)
        holder.textView.text = itemsViewModel.text
    }

    override fun getItemCount(): Int = getList.size

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        itemClickListener = listener
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.gameImage)
        val textView: TextView = itemView.findViewById(R.id.gameName)

    }
}