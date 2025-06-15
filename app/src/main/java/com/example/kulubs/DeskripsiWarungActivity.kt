package com.example.kulubs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kulubs.adapter.MenuAdapter
import com.example.kulubs.adapter.ReviewAdapter
import com.example.kulubs.model.MenuModel
import com.example.kulubs.model.Review
import com.example.kulubs.util.CloudinaryConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class DeskripsiWarungActivity : AppCompatActivity() {
    private var isLiked = false
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var tvEmptyMenu: TextView
    private lateinit var menuAdapter: MenuAdapter
    private val menuList = mutableListOf<MenuModel>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var menuListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deskripsi_warung)

        // Inisialisasi Cloudinary
        try {
            CloudinaryConfig.init(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal inisialisasi Cloudinary: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        recyclerMenu = findViewById(R.id.recyclerMenu)
        tvEmptyMenu = findViewById(R.id.tvEmptyMenu)
        val thumbsUpButton = findViewById<ImageButton>(R.id.thumbsUpButton)
        val btnTambahMenu = findViewById<ImageButton>(R.id.btnTambahMenu)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }
        thumbsUpButton.setOnClickListener {
            isLiked = !isLiked
            thumbsUpButton.setImageResource(if (isLiked) R.drawable.ic_thumbs_up_2 else R.drawable.ic_thumbs_up)
        }

        recyclerMenu.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(this, menuList, { editMenu(it) }, { deleteMenu(it) })
        recyclerMenu.adapter = menuAdapter
        updateEmptyMenuVisibility()

        // Ambil menu dari Firestore
        fetchMenus()

        btnTambahMenu.setOnClickListener {
            val intent = Intent(this, AddEditMenuActivity::class.java)
            addMenuLauncher.launch(intent)
        }

        val recyclerReview = findViewById<RecyclerView>(R.id.recyclerReview)
        recyclerReview.layoutManager = LinearLayoutManager(this)
        val dummyReviews = listOf(
            Review("Ali", 5, "10 Apr 2025", 4f, "Enak banget makanannya!", 12),
            Review("Budi", 3, "11 Apr 2025", 5f, "Mantap pokoknya, wajib coba!", 20),
            Review("Cici", 8, "12 Apr 2025", 3f, "Agak ramai tapi worth it", 8)
        )
        recyclerReview.adapter = ReviewAdapter(this, dummyReviews)
    }

    private fun fetchMenus() {
        menuListener = db.collection("warungs").document("WARUNG_ID").collection("menus")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                menuList.clear()
                snapshots?.forEach { document ->
                    try {
                        val menu = document.toObject(MenuModel::class.java)
                        menuList.add(menu)
                    } catch (ex: Exception) {
                        Toast.makeText(this, "Gagal memuat menu: ${ex.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                menuAdapter.notifyDataSetChanged()
                updateEmptyMenuVisibility()
            }
    }

    private val addMenuLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
        }
    }

    private fun editMenu(menu: MenuModel) {
        val intent = Intent(this, AddEditMenuActivity::class.java)
        intent.putExtra("MENU", menu)
        addMenuLauncher.launch(intent)
    }

    private fun deleteMenu(menu: MenuModel) {
        db.collection("warungs").document("WARUNG_ID").collection("menus").document(menu.id.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Menu berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateEmptyMenuVisibility() {
        tvEmptyMenu.visibility = if (menuList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        menuListener.remove() // Bersihkan listener Firestore
    }
}