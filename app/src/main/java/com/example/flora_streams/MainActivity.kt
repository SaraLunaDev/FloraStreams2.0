package com.example.flora_streams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.flora_streams.adapter.CategoryAdapter
import com.example.flora_streams.data.FloraDatabase
import com.example.flora_streams.files.Category
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var api: ApiService
    private lateinit var rvCategories: RecyclerView
    private lateinit var progressBar: LinearLayout
    private lateinit var tvEmpty: TextView
    private lateinit var db: FloraDatabase
    private lateinit var reloadJsonLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvCategories = findViewById(R.id.rvCategories)
        progressBar = findViewById(R.id.progressBar)

        rvCategories.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ejemplo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)

        swipeRefresh.setOnRefreshListener {
            loadData(true)
        }

        reloadJsonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            loadData(isSwipe = false)
        }

        val topBar: MaterialToolbar = findViewById(R.id.topBar)
        setSupportActionBar(topBar)

        tvEmpty = findViewById(R.id.tvEmpty)
        db = FloraDatabase.getDatabase(this)

        loadData(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_json_list -> {
                val intent = Intent(this, JsonManagerActivity::class.java)
                reloadJsonLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadData(isSwipe: Boolean) {
        if (!isSwipe) progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val urls = db.jsonUrlDao().getAll()

            if (urls.isEmpty()) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    tvEmpty.visibility = View.VISIBLE
                    rvCategories.visibility = View.GONE
                }
            } else {
                val firstUrl = urls.first()
                try {
                    val response = api.getCategories(firstUrl.url).execute()
                    val lista = if (response.isSuccessful) response.body() else emptyList()

                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false

                        if (lista.isNullOrEmpty()) {
                            tvEmpty.visibility = View.VISIBLE
                            rvCategories.visibility = View.GONE
                        } else {
                            tvEmpty.visibility = View.GONE
                            rvCategories.visibility = View.VISIBLE
                            rvCategories.adapter = CategoryAdapter(lista)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                        tvEmpty.visibility = View.VISIBLE
                        rvCategories.visibility = View.GONE
                    }
                }
            }
        }
    }
}
