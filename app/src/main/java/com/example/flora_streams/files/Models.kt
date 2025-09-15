package com.example.flora_streams.files

data class Url(
    val name: String,
    val url: String
)

data class Subcategory(
    val name: String,
    val icon: String,
    val urls: List<Url>
)

data class Category(
    val name: String,
    val subcategories: List<Subcategory>
)