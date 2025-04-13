package com.example.kulubs.repository

import android.util.Log
import com.example.kulubs.model.FilterItem
import com.example.kulubs.model.WarungItem
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreRepository"

    suspend fun searchWarungs(query: String): List<WarungItem> {
        return withContext(Dispatchers.IO) {
            try {
                val queryLower = query.lowercase()

                val snapshot = db.collection("warungs")
                    .get()
                    .await()

                snapshot.documents
                    .mapNotNull { document ->
                        document.toObject(WarungItem::class.java)?.apply {
                            id = document.id
                        }
                    }
                    .filter { warung ->
                        warung.name.lowercase().contains(queryLower) ||
                                warung.address.lowercase().contains(queryLower) ||
                                warung.categories.any { it.lowercase().contains(queryLower) }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error searching warungs", e)
                emptyList()
            }
        }
    }

    suspend fun getRatingFilterOptions(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("ratings").document("rating_options").get().await()
                document.get("options") as? List<String> ?: listOf("Semua", "5 Bintang", "4+ Bintang", "3+ Bintang")
            } catch (e: Exception) {
                Log.e(TAG, "Error getting rating options", e)
                listOf("Semua", "5 Bintang", "4+ Bintang", "3+ Bintang")
            }
        }
    }

    suspend fun getFoodCategoryOptions(): List<FilterItem> {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("categories").document("food_categories").get().await()
                val options = document.get("options") as? List<String> ?:
                listOf("Semua", "Mie", "Nusantara", "Nasi", "Western", "Japanese")

                options.mapIndexed { index, text ->
                    FilterItem(
                        id = index + 2,
                        text = text,
                        isSelected = index == 0,
                        isSpinner = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting food categories", e)
                listOf(
                    FilterItem(2, "Semua", isSelected = true, isSpinner = false),
                    FilterItem(3, "Mie", isSelected = false, isSpinner = false),
                    FilterItem(4, "Nusantara", isSelected = false, isSpinner = false),
                    FilterItem(5, "Nasi", isSelected = false, isSpinner = false),
                    FilterItem(6, "Western", isSelected = false, isSpinner = false),
                    FilterItem(7, "Japanese", isSelected = false, isSpinner = false)
                )
            }
        }
    }

    suspend fun getAllWarungs(): List<WarungItem> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection("warungs").get().await()
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject(WarungItem::class.java)?.apply {
                        id = document.id
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting warungs", e)
                emptyList()
            }
        }
    }

    suspend fun getWarungsByCategory(category: String): List<WarungItem> {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = if (category == "Semua") {
                    db.collection("warungs").get().await()
                } else {
                    db.collection("warungs")
                        .whereArrayContains("categories", category)
                        .get()
                        .await()
                }

                querySnapshot.documents.mapNotNull { document ->
                    document.toObject(WarungItem::class.java)?.apply {
                        id = document.id
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting warungs by category", e)
                emptyList()
            }
        }
    }

    suspend fun getWarungsFiltered(category: String?, minRating: Float?): List<WarungItem> {
        return withContext(Dispatchers.IO) {
            try {
                var query = db.collection("warungs")

                if (minRating != null && minRating > 0) {
                    query = query.whereGreaterThanOrEqualTo("rating", minRating) as CollectionReference
                }

                val querySnapshot = query.get().await()

                var results = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(WarungItem::class.java)?.apply {
                        id = document.id
                    }
                }

                if (category != null && category != "Semua") {
                    results = results.filter { warung ->
                        warung.categories.contains(category)
                    }
                }

                results
            } catch (e: Exception) {
                Log.e(TAG, "Error getting filtered warungs", e)
                emptyList()
            }
        }
    }
}