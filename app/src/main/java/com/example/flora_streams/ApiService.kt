package com.example.flora_streams

import com.example.flora_streams.files.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    fun getCategories(@Url url: String): Call<List<Category>>
}