package com.example.kulubs.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.kulubs.R
import com.example.kulubs.model.MenuModel

class MenuAdapter(
    private val context: Context,
    private val menuList: List<MenuModel>,
    private val onEditClick: (MenuModel) -> Unit,
    private val onDeleteClick: (MenuModel) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMenuImage: ImageView = itemView.findViewById(R.id.ivMenuImage)
        val tvNamaMenu: TextView = itemView.findViewById(R.id.tvNamaMenu)
        val tvDeskripsiMenu: TextView = itemView.findViewById(R.id.tvDeskripsiMenu)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHargaMenu)
        val btnEditMenu: ImageButton = itemView.findViewById(R.id.btnEditMenu)
        val btnDeleteMenu: ImageButton = itemView.findViewById(R.id.btnDeleteMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]
        holder.tvNamaMenu.text = menu.nama
        holder.tvDeskripsiMenu.text = menu.deskripsi
        val formattedHarga = String.format("Rp %,d", menu.harga.toInt())
        holder.tvHarga.text = formattedHarga

        if (!menu.gambarPath.isNullOrEmpty()) {
            val imageUrl = menu.gambarPath!!.replace("http://", "https://")
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(holder.ivMenuImage)
        } else {
            holder.ivMenuImage.setImageResource(R.drawable.placeholder_food)
        }

        holder.btnEditMenu.setOnClickListener { onEditClick(menu) }
        holder.btnDeleteMenu.setOnClickListener { onDeleteClick(menu) }
    }

    override fun getItemCount(): Int = menuList.size
}