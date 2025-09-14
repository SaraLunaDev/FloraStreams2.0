package com.example.flora_streams

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.flora_streams.adapter.CategoryAdapter
import com.example.flora_streams.files.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var api: ApiService
    private lateinit var rvCategories: RecyclerView
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvCategories = findViewById(R.id.rvCategories)
        progressBar = findViewById(R.id.progressBar)

        rvCategories.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder().baseUrl("https://gist.githubusercontent.com/").addConverterFactory(
            GsonConverterFactory.create()).build()
        api = retrofit.create(ApiService::class.java)

        swipeRefresh.setOnRefreshListener {
            loadData(true)
        }

        loadData(false)
    }

    private fun loadData(isSwipe: Boolean) {
        if (!isSwipe) {
            progressBar.visibility = View.VISIBLE
        }

        api.getCategories("https://gist.githubusercontent.com/SaraLunaDev/dd8a4637526993e11aa7b6b5c14bfd45/raw/data.json").enqueue(object : Callback<List<Category>> {
            override fun onResponse(
                call: Call<List<Category>?>,
                response: Response<List<Category>?>
            ) {
                if (isSwipe) swipeRefresh.isRefreshing = false
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
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

            override fun onFailure(call: Call<List<Category>?>, t: Throwable) {
                if (isSwipe) swipeRefresh.isRefreshing = false
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
