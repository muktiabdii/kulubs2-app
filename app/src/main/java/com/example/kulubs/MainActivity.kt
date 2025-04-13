package com.example.kulubs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kulubs.util.FirestoreDataInitializer
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Edge-to-edge handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Initialize data (only once)
        initializeFirestoreData()

        // 2. Set up search button
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun initializeFirestoreData() {
        lifecycleScope.launch {
            try {
                if (FirestoreDataInitializer.shouldInitialize(this@MainActivity)) {
                    Log.d("MainActivity", "Initializing sample data...")
                    FirestoreDataInitializer(this@MainActivity).initializeDatabase()
                    FirestoreDataInitializer.markInitialized(this@MainActivity)
                } else {
                    Log.d("MainActivity", "Data already initialized")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to initialize data", e)
            }
        }
    }
}