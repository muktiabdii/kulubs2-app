package com.example.kulubs

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val query = intent.getStringExtra("QUERY") ?: ""

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(query)

        searchEditText.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        val resultText = findViewById<TextView>(R.id.resultText)
        resultText.text = "Hasil pencarian untuk: $query"

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {

            val intent = Intent(this, SearchResultActivity::class.java)
            intent.putExtra("QUERY", trimmedQuery)
            startActivity(intent)
            finish()
        }
    }
}