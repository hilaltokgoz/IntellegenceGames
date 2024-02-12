package com.gloory.intellegencegames.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gloory.intellegencegames.R

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 11.02.2024               │
//└──────────────────────────┘

class HomePageCustomAdapter(
    private val getList: List<HomePageItem>,
    private var itemClickListener: (item: HomePageItem) -> Unit
) :
    RecyclerView.Adapter<HomePageCustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getList[position]

        holder.imageView.setImageResource(item.imageId)
        holder.textView.text = item.text
        holder.cardView.setOnClickListener {
            itemClickListener(item)
        }
    }

    override fun getItemCount(): Int = getList.size

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.gameImage)
        val textView: TextView = itemView.findViewById(R.id.gameName)
        val cardView: CardView = itemView.findViewById(R.id.cardViewContainer)

    }
}