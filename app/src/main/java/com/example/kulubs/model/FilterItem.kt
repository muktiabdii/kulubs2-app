package com.example.kulubs.model

data class FilterItem(
    val id: Int,
    val text: String,
    var isSelected: Boolean = false,
    val isSpinner: Boolean = false
)