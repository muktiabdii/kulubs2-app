package com.example.kulubs.util

import android.content.Context
import android.util.Log
import com.example.kulubs.model.WarungItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreDataInitializer(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreDataInitializer"

    suspend fun initializeDatabase() {
        withContext(Dispatchers.IO) {
            try {
                // Check if data already exists
                val categoriesDoc = db.collection("categories").document("food_categories").get().await()
                if (categoriesDoc.exists()) {
                    Log.d(TAG, "Database already initialized, skipping")
                    return@withContext
                }

                // Initialize data
                initializeCategories()
                initializeRatingOptions()
                initializeSampleWarungs()

                Log.d(TAG, "Database initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing database", e)
            }
        }
    }

    private suspend fun initializeCategories() {
        val categories = hashMapOf(
            "options" to listOf("Semua", "Mie", "Nusantara", "Nasi", "Western", "Japanese", "Korean", "Snack")
        )

        db.collection("categories").document("food_categories")
            .set(categories)
            .await()
    }

    private suspend fun initializeRatingOptions() {
        val ratingOptions = hashMapOf(
            "options" to listOf("Semua", "5 Bintang", "4+ Bintang", "3+ Bintang")
        )

        db.collection("ratings").document("rating_options")
            .set(ratingOptions)
            .await()
    }

    private suspend fun initializeSampleWarungs() {
        val sampleWarungs = listOf(
            WarungItem(
                name = "Lalapan Mbak L",
                rating = 4.9f,
                likes = 200,
                address = "Kantin FILKOM di belakang GKM",
                categories = listOf("Nusantara", "Nasi", "Lalapan"),
                imageUrl = "https://statik.tempo.co/data/2022/10/26/id_1151821/1151821_720.jpg",
                whatsappLink = "https://wa.me/628123456789"
            ),
            WarungItem(
                name = "Mie Ayam Bakso Pak Agus",
                rating = 4.7f,
                likes = 176,
                address = "Kantin FILKOM sebelah kiri",
                categories = listOf("Mie", "Bakso"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/mie_ayam_bakso.jpg?alt=media",
                whatsappLink = "https://wa.me/628234567890"
            ),
            WarungItem(
                name = "Warung Padang Sederhana",
                rating = 4.5f,
                likes = 145,
                address = "Depan Fakultas Kedokteran",
                categories = listOf("Nusantara", "Nasi", "Padang"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/warung_padang.jpg?alt=media",
                whatsappLink = "https://wa.me/628345678901"
            ),
            WarungItem(
                name = "Kebab Turki Bu Siti",
                rating = 4.6f,
                likes = 133,
                address = "Samping Perpustakaan Pusat",
                categories = listOf("Western", "Kebab"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/kebab_turki.jpg?alt=media",
                whatsappLink = "https://wa.me/628456789012"
            ),
            WarungItem(
                name = "Sushi Express",
                rating = 4.8f,
                likes = 112,
                address = "Depan gedung C FILKOM",
                categories = listOf("Japanese", "Sushi"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/sushi_express.jpg?alt=media",
                whatsappLink = "https://wa.me/628567890123"
            ),
            WarungItem(
                name = "Nasi Goreng Pak Budi",
                rating = 4.4f,
                likes = 98,
                address = "Kantin FIA lantai 1",
                categories = listOf("Nasi", "Nusantara"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/nasi_goreng.jpg?alt=media",
                whatsappLink = "https://wa.me/628678901234"
            ),
            WarungItem(
                name = "Korean BBQ Mini",
                rating = 4.6f,
                likes = 87,
                address = "Gedung E lantai 2 FILKOM",
                categories = listOf("Korean", "BBQ"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/korean_bbq.jpg?alt=media",
                whatsappLink = "https://wa.me/628789012345"
            ),
            WarungItem(
                name = "Cireng & Seblak Mba Dina",
                rating = 4.3f,
                likes = 76,
                address = "Samping Masjid Raden Patah",
                categories = listOf("Snack", "Nusantara"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/cireng_seblak.jpg?alt=media",
                whatsappLink = "https://wa.me/628890123456"
            ),
            WarungItem(
                name = "Pizza Mini FKM",
                rating = 4.5f,
                likes = 65,
                address = "Kantin FKM lantai 1",
                categories = listOf("Western", "Pizza"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/pizza_mini.jpg?alt=media",
                whatsappLink = "https://wa.me/628901234567"
            ),
            WarungItem(
                name = "Warung Tegal Barokah",
                rating = 4.2f,
                likes = 54,
                address = "Belakang GKB I",
                categories = listOf("Nusantara", "Nasi"),
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/kulubs.appspot.com/o/warteg.jpg?alt=media",
                whatsappLink = "https://wa.me/628012345678"
            )
        )

        // Add each warung to Firestore
        for (warung in sampleWarungs) {
            db.collection("warungs").add(warung).await()
        }
    }

    companion object {
        fun shouldInitialize(context: Context): Boolean {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            return !prefs.getBoolean("db_initialized", false)
        }

        fun markInitialized(context: Context) {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("db_initialized", true).apply()
        }
    }
}