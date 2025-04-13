package com.example.kulubs.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kulubs.R
import com.example.kulubs.model.WarungItem
import com.google.android.material.button.MaterialButton

class WarungAdapter(
    private val onItemClick: (WarungItem) -> Unit,
    private val onReviewClick: (WarungItem) -> Unit
) : ListAdapter<WarungItem, WarungAdapter.WarungViewHolder>(WarungDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarungViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hasil, parent, false)
        return WarungViewHolder(view, onItemClick, onReviewClick)
    }

    override fun onBindViewHolder(holder: WarungViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WarungViewHolder(
        itemView: View,
        private val onItemClick: (WarungItem) -> Unit,
        private val onReviewClick: (WarungItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imgWarung: ImageView = itemView.findViewById(R.id.img_warung)
        private val tvNamaWarung: TextView = itemView.findViewById(R.id.tv_nama_warung)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        private val tvLikes: TextView = itemView.findViewById(R.id.tv_likes)
        private val tvAlamat: TextView = itemView.findViewById(R.id.tv_deskripsi)
        private val tvKategori: TextView = itemView.findViewById(R.id.tv_kategori)
        private val btnWhatsapp: MaterialButton = itemView.findViewById(R.id.btn_whatsapp)
        private val btnReview: MaterialButton = itemView.findViewById(R.id.btn_review)

        fun bind(warung: WarungItem) {
            tvNamaWarung.text = warung.name
            tvRating.text = " ${warung.rating}"
            tvLikes.text = "Disukai ${warung.likes}"
            tvAlamat.text = warung.address
            tvKategori.text = warung.categories.joinToString(", ")

            // Set star rating based on the rating value
            // This would require you to have access to the ImageView stars
            // We're skipping this part as it depends on how you implement the star rating UI

            // Load image with Glide
            Glide.with(itemView.context)
                .load(warung.imageUrl)
                .placeholder(R.drawable.sample_warung)
                .error(R.drawable.sample_warung)
                .into(imgWarung)

            // Set WhatsApp button click listener
            btnWhatsapp.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(warung.whatsappLink)
                    itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    // Handle error (WhatsApp not installed, etc.)
                }
            }

            // Set Review button click listener
            btnReview.setOnClickListener {
                onReviewClick(warung)
            }

            // Set card click listener
            itemView.setOnClickListener {
                onItemClick(warung)
            }
        }
    }

    private class WarungDiffCallback : DiffUtil.ItemCallback<WarungItem>() {
        override fun areItemsTheSame(oldItem: WarungItem, newItem: WarungItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WarungItem, newItem: WarungItem): Boolean {
            return oldItem == newItem
        }
    }
}