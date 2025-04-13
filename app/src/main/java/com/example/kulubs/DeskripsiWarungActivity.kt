package com.example.kulubs

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class DeskripsiWarungActivity : AppCompatActivity() {
    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deskripsi_warung) // Sesuaikan dengan layout yang benar

        val thumbsUpButton = findViewById<ImageButton>(R.id.thumbsUpButton)

        thumbsUpButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                thumbsUpButton.setImageResource(R.drawable.ic_thumbs_up_2) // Ikon berwarna
            } else {
                thumbsUpButton.setImageResource(R.drawable.ic_thumbs_up) // Ikon stroke
            }
        }
    }
}
