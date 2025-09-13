package com.example.flora_streams

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flora_streams.adapter.CategoryAdapter
import com.example.flora_streams.files.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder().baseUrl("https://gist.githubusercontent.com/").addConverterFactory(
            GsonConverterFactory.create()).build()

        val api = retrofit.create(ApiService::class.java)

        api.getCategories("https://gist.githubusercontent.com/SaraLunaDev/dd8a4637526993e11aa7b6b5c14bfd45/raw/data.json")
            .enqueue(object : Callback<List<Category>> {
                override fun onResponse(
                    call: Call<List<Category>?>,
                    response: retrofit2.Response<List<Category>?>
                ) {
                    if (response.isSuccessful){
                        val lista = response.body()

                        lista?.let {
                            rvCategories.adapter = CategoryAdapter(it)
                        }

                        lista?.forEach { category ->
                            Log.d("CATEGORY", "Categoria: ${category.name}")
                            category.subcategories.forEach { sub ->
                                Log.d("SUBCATEGORY","Subcategoria: ${sub.name}")
                                sub.urls.forEach { url ->
                                    Log.d("URL","Url: ${url.name} -> ${url.url}")
                                }
                            }
                        }
                    }
                    else{
                        Log.e("API", "Error en la respuesta: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<List<Category>?>,
                    t: Throwable
                ) {
                    Log.e("API", "Error: ${t.message}")
                }

            })
    }
}
