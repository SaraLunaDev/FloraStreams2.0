package com.example.flora_streams.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface JsonUrlDao {
    @Query("SELECT * FROM json_urls")
    suspend fun getAll(): List<JsonUrlEntity>

    @Insert
    suspend fun insert(jsonUrl: JsonUrlEntity)

    @Update
    suspend fun update(jsonUrl: JsonUrlEntity)

    @Delete
    suspend fun delete(jsonUrl: JsonUrlEntity)
}