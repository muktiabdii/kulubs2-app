package com.example.kulubs.adapter

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kulubs.R
import com.example.kulubs.model.FilterItem
import com.google.android.material.button.MaterialButton

class FilterAdapter(
    private val items: List<FilterItem>,
    private val onItemClick: (FilterItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SPINNER = 0
        private const val TYPE_FILTER = 1
        private const val TAG = "FilterAdapter"
    }

    init {
        // Log items when adapter is created
        Log.d(TAG, "Adapter initialized with ${items.size} items")
        items.forEachIndexed { index, item ->
            Log.d(TAG, "Item $index: ${item.text}, isSpinner: ${item.isSpinner}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder called with viewType: $viewType")

        return when (viewType) {
            TYPE_SPINNER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_spinner, parent, false)
                SpinnerViewHolder(view, onItemClick)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_filter, parent, false)
                FilterViewHolder(view, onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        Log.d(TAG, "Binding item at position $position: ${item.text}, isSpinner: ${item.isSpinner}")

        when (holder) {
            is SpinnerViewHolder -> holder.bind(item)
            is FilterViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val viewType = if (items[position].isSpinner) TYPE_SPINNER else TYPE_FILTER
        Log.d(TAG, "getItemViewType for position $position: $viewType")
        return viewType
    }

    class SpinnerViewHolder(
        itemView: View,
        private val onItemClick: (FilterItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val button: MaterialButton = itemView.findViewById(R.id.btnFilter)

        fun bind(item: FilterItem) {
            button.text = item.text

            // Atur warna berdasarkan status seleksi
            if (item.isSelected) {
                button.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.filter_active)
                )
                button.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            } else {
                button.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.filter_inactive_bg)
                )
                button.setTextColor(ContextCompat.getColor(itemView.context, R.color.filter_inactive_text))
            }

            button.setOnClickListener { onItemClick(item) }
        }
    }

    class FilterViewHolder(
        itemView: View,
        private val onItemClick: (FilterItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val button: MaterialButton = itemView.findViewById(R.id.btnFilter)

        fun bind(item: FilterItem) {
            button.text = item.text

            // Atur warna berdasarkan status seleksi
            if (item.isSelected) {
                button.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.filter_active)
                )
                button.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            } else {
                button.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.filter_inactive_bg)
                )
                button.setTextColor(ContextCompat.getColor(itemView.context, R.color.filter_inactive_text))
            }

            button.setOnClickListener { onItemClick(item) }
        }
    }
}