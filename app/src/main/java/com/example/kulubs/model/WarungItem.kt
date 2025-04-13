package com.example.kulubs.model

data class WarungItem(
    var id: String? = null,
    val name: String = "",
    val rating: Float = 0.0f,
    val likes: Int = 0,
    val address: String = "",
    val categories: List<String> = listOf(),
    val imageUrl: String = "",
    val whatsappLink: String = ""
)