package com.example.kulubs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kulubs.adapter.FilterAdapter
import com.example.kulubs.adapter.WarungAdapter
import com.example.kulubs.model.FilterItem
import com.example.kulubs.model.WarungItem
import com.example.kulubs.repository.FirestoreRepository
import kotlinx.coroutines.launch

class SearchResultActivity : AppCompatActivity() {
    private lateinit var filterItems: MutableList<FilterItem>
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var warungAdapter: WarungAdapter
    private val firestoreRepository = FirestoreRepository()

    // Keep track of current filters
    private var selectedCategories: MutableSet<String> = mutableSetOf()
    private var currentMinRating: Float = 0f

    // Original unfiltered list
    private var allWarungs: List<WarungItem> = listOf()

    // Constants
    private val SEMUA_FILTER_TEXT = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                performSearch(query)
            } else {
                Toast.makeText(this, "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show()
            }
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya (SearchActivity)
        }

        // Get the search query from intent
        val searchQuery = intent.getStringExtra("QUERY") ?: ""

        // Set up the search EditText with the query

        searchEditText.setText(searchQuery)

        // Set the result text with the query
        val resultText = findViewById<TextView>(R.id.resultText)
        resultText.text = "Hasil pencarian untuk \"$searchQuery\""

        // Set up search functionality
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val newQuery = searchEditText.text.toString().trim()
                if (newQuery.isNotEmpty()) {
                    // Update the result text
                    resultText.text = "Hasil pencarian untuk \"$newQuery\""
                    // Perform new search
                    performSearch(newQuery)
                }
                true
            } else {
                false
            }
        }

        setupFiltersRecyclerView()
        setupWarungRecyclerView()
        loadDataFromFirestore()
    }

    private fun performSearch(query: String) {
        // Implement your search logic here
        // You can filter the existing data or fetch new data from Firestore
        lifecycleScope.launch {
            try {
                val filteredWarungs = firestoreRepository.searchWarungs(query)
                warungAdapter.submitList(filteredWarungs)

                // Update result text
                findViewById<TextView>(R.id.resultText).text =
                    "Menampilkan ${filteredWarungs.size} hasil untuk \"$query\""
            } catch (e: Exception) {
                Log.e("SearchResultActivity", "Error searching warungs", e)
                Toast.makeText(this@SearchResultActivity,
                    "Error melakukan pencarian", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFiltersRecyclerView() {
        val rvFilters = findViewById<RecyclerView>(R.id.rv_filters)
        rvFilters.setHasFixedSize(true)
        rvFilters.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize with "Semua" selected by default
        filterItems = mutableListOf(
            FilterItem(1, "⭐", isSelected = false, isSpinner = true),
            FilterItem(2, "Semua", isSelected = true, isSpinner = false)
        )

        filterAdapter = FilterAdapter(filterItems) { selectedItem ->
            if (!selectedItem.isSpinner) {
                val semuaItem = filterItems.find { it.id == 2 }!!

                when {
                    // Case 1: Clicking "Semua"
                    selectedItem.id == 2 -> {
                        if (!selectedItem.isSelected) {
                            // Deselect all others and select "Semua"
                            filterItems.forEach {
                                it.isSelected = it.id == 2
                            }
                            selectedCategories = mutableSetOf("Semua")
                            filterAdapter.notifyDataSetChanged()
                        }
                    }

                    // Case 2: Clicking an already selected filter (deselecting)
                    selectedItem.isSelected -> {
                        selectedItem.isSelected = false
                        selectedCategories.remove(selectedItem.text)

                        // If no filters left selected, default to "Semua"
                        if (selectedCategories.isEmpty()) {
                            semuaItem.isSelected = true
                            selectedCategories.add("Semua")
                            filterAdapter.notifyDataSetChanged()
                        } else {
                            filterAdapter.notifyItemChanged(filterItems.indexOf(selectedItem))
                        }
                    }

                    // Case 3: Selecting a new filter
                    else -> {
                        // Deselect "Semua" if it was selected
                        if (semuaItem.isSelected) {
                            semuaItem.isSelected = false
                            selectedCategories.remove("Semua")
                            filterAdapter.notifyItemChanged(filterItems.indexOf(semuaItem))
                        }

                        selectedItem.isSelected = true
                        selectedCategories.add(selectedItem.text)
                        filterAdapter.notifyItemChanged(filterItems.indexOf(selectedItem))
                    }
                }

                applyFilters()
            } else {
                showRatingFilterOptions()
            }
        }

        rvFilters.adapter = filterAdapter
    }

    private fun setupWarungRecyclerView() {
        val rvWarungs = findViewById<RecyclerView>(R.id.rv_hasil)
        rvWarungs.layoutManager = LinearLayoutManager(this)

        warungAdapter = WarungAdapter(
            onItemClick = { warung ->
                // Handle warung item click - navigate to detail page
                navigateToWarungDetail(warung)
            },
            onReviewClick = { warung ->
                // Handle review button click
                navigateToWriteReview(warung)
            }
        )

        rvWarungs.adapter = warungAdapter
    }

    private fun navigateToWarungDetail(warung: WarungItem) {
        Toast.makeText(this, "Membuka detail: ${warung.name}", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToWriteReview(warung: WarungItem) {
        Toast.makeText(this, "Menulis review untuk: ${warung.name}", Toast.LENGTH_SHORT).show()
    }

    private fun loadDataFromFirestore() {
        lifecycleScope.launch {
            try {
                // Load food categories
                val categories = firestoreRepository.getFoodCategoryOptions()

                // Start with spinner filter
                filterItems.clear()
                filterItems.add(FilterItem(1, "⭐", isSelected = false, isSpinner = true))

                // Add "Semua" filter first (selected by default)
                val semuaFilter = categories.find { it.text == "Semua" }
                    ?: FilterItem(2, "Semua", isSelected = true, isSpinner = false)

                // Make sure Semua is selected by default
                semuaFilter.isSelected = true
                filterItems.add(semuaFilter)

                // Add remaining categories (not selected by default)
                categories
                    .filter { it.text != "Semua" }
                    .forEach { filterItems.add(it.copy(isSelected = false)) }

                // Update selected categories
                selectedCategories = mutableSetOf("Semua")

                // Notify adapter about new items
                filterAdapter.notifyDataSetChanged()

                // Load warung data
                allWarungs = firestoreRepository.getAllWarungs()
                warungAdapter.submitList(allWarungs)

                Log.d("SearchResultActivity", "Loaded ${allWarungs.size} warungs and ${categories.size} categories")
            } catch (e: Exception) {
                Log.e("SearchResultActivity", "Error loading data", e)
                Toast.makeText(this@SearchResultActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRatingFilterOptions() {
        lifecycleScope.launch {
            try {
                val ratingOptions = firestoreRepository.getRatingFilterOptions()

                AlertDialog.Builder(this@SearchResultActivity)
                    .setTitle("Filter berdasarkan Rating")
                    .setItems(ratingOptions.toTypedArray()) { dialog, which ->
                        // Apply selected rating filter
                        val selectedOption = ratingOptions[which]
                        val ratingValue = when (selectedOption) {
                            "Semua" -> 0f
                            "5 Bintang" -> 5f
                            "4+ Bintang" -> 4f
                            "3+ Bintang" -> 3f
                            else -> 0f
                        }

                        currentMinRating = ratingValue

                        // Update spinner text
                        val spinnerItem = filterItems[0]
                        val newSpinnerItem = FilterItem(
                            id = spinnerItem.id,
                            text = if (which == 0) "⭐" else "⭐ ${selectedOption}",
                            isSelected = which != 0,
                            isSpinner = true
                        )
                        filterItems[0] = newSpinnerItem
                        filterAdapter.notifyItemChanged(0)

                        // Apply filters
                        applyFilters()
                    }
                    .show()
            } catch (e: Exception) {
                Log.e("SearchResultActivity", "Error showing rating options", e)
                Toast.makeText(this@SearchResultActivity, "Error loading rating options", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyFilters() {
        lifecycleScope.launch {
            try {
                // For each selected category, fetch filtered results and combine
                val results = mutableListOf<WarungItem>()

                if (selectedCategories.contains(SEMUA_FILTER_TEXT) || selectedCategories.isEmpty()) {
                    // If "Semua" is selected, get all results with just the rating filter
                    val filteredWarungs = firestoreRepository.getWarungsFiltered(
                        category = null,
                        minRating = if (currentMinRating == 0f) null else currentMinRating
                    )
                    results.addAll(filteredWarungs)
                } else {
                    // Get filtered results for each selected category
                    val processedWarungIds = mutableSetOf<String>() // To avoid duplicates

                    for (category in selectedCategories) {
                        val filteredWarungs = firestoreRepository.getWarungsFiltered(
                            category = category,
                            minRating = if (currentMinRating == 0f) null else currentMinRating
                        )

                        // Add warungs that haven't been added yet
                        for (warung in filteredWarungs) {
                            // Check if the warung has a non-null ID and hasn't been processed yet
                            val warungId = warung.id
                            if (warungId != null && !processedWarungIds.contains(warungId)) {
                                results.add(warung)
                                processedWarungIds.add(warungId)
                            }
                        }
                    }
                }

                // Update adapter with combined results
                warungAdapter.submitList(results)

                // Show filter feedback
                val categoryStr = if (selectedCategories.contains(SEMUA_FILTER_TEXT) || selectedCategories.isEmpty()) {
                    "semua kategori"
                } else {
                    selectedCategories.joinToString(", ")
                }

                val ratingStr = if (currentMinRating == 0f) "semua rating" else "$currentMinRating+ bintang"

                Toast.makeText(
                    this@SearchResultActivity,
                    "Menampilkan $categoryStr dengan $ratingStr (${results.size} hasil)",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("SearchResultActivity", "Error applying filters", e)
                Toast.makeText(this@SearchResultActivity, "Error filtering results", Toast.LENGTH_SHORT).show()
            }
        }
    }
}