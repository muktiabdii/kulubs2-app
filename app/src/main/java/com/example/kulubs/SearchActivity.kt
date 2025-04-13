package com.example.kulubs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private val MAX_HISTORY = 5
    // Deklarasikan variabel di level class
    private lateinit var historyListView: ListView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Inisialisasi views
        historyListView = findViewById(R.id.lvHistory)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)
        searchEditText = findViewById(R.id.searchEditText)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        val tvHapus = findViewById<TextView>(R.id.tvHapus)

        // Display search history
        displaySearchHistory()

        // 1. Handle search button click
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                saveSearchQuery(query)
                performSearch(query)
            } else {
                Toast.makeText(this, "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show()
            }
        }

        tvHapus.setOnClickListener {
            clearSearchHistory()
        }

        // Tampilkan riwayat pencarian
        displaySearchHistory()

        // Tambahkan IME options
        searchEditText.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT

        // Implementasi listener untuk aksi keyboard
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    // Simpan query ke riwayat
                    saveSearchQuery(query)
                    // Lakukan pencarian
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        val intent = Intent(this, SearchResultActivity::class.java).apply {
            putExtra("QUERY", query)
            // Clear the activity stack so back button works properly
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish() // Close the search activity
    }

    private fun saveSearchQuery(query: String) {
        val prefs = getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)

        // Ambil riwayat yang ada
        val historySet = prefs.getStringSet("history", HashSet<String>())?.toMutableSet()
            ?: mutableSetOf()

        // Tambahkan query baru (hapus dulu jika sudah ada untuk menghindari duplikat)
        historySet.remove(query)
        historySet.add(query)

        // Jika lebih dari MAX_HISTORY, hapus yang paling lama
        if (historySet.size > MAX_HISTORY) {
            val historyList = historySet.toMutableList()
            historyList.removeAt(0) // Hapus item pertama (paling lama)
            historySet.clear()
            historySet.addAll(historyList)
        }

        // Simpan kembali ke SharedPreferences
        prefs.edit().putStringSet("history", historySet).apply()
    }

    private fun clearSearchHistory() {
        val prefs = getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
        prefs.edit().remove("history").apply()

        tvEmptyHistory.visibility = TextView.VISIBLE
        historyListView.visibility = ListView.GONE
    }

    private fun displaySearchHistory() {
        val prefs = getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
        val historySet = prefs.getStringSet("history", HashSet<String>()) ?: HashSet()
        val historyList = historySet.toList().reversed().take(5)

        if (historyList.isEmpty()) {
            tvEmptyHistory.visibility = TextView.VISIBLE
            historyListView.visibility = ListView.GONE
        } else {
            tvEmptyHistory.visibility = TextView.GONE
            historyListView.visibility = ListView.VISIBLE
            val adapter = ArrayAdapter(this, R.layout.item_history, R.id.tvHistoryItem, historyList)
            historyListView.adapter = adapter

            historyListView.setOnItemClickListener { _, _, position, _ ->
                val query = historyList[position]
                Log.d("SearchActivity", "Item clicked: $query")
                performSearch(query)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Perbarui riwayat pencarian setiap kali activity dibuka kembali
        displaySearchHistory()
    }
}