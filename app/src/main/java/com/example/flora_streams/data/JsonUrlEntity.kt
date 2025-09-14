package com.example.flora_streams.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "json_urls")
data class JsonUrlEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String
)
