package com.example.flora_streams

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.flora_streams.adapter.CategoryAdapter
import com.example.flora_streams.data.FloraDatabase
import com.example.flora_streams.utils.TvScrollOffsetManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var api: ApiService
    private lateinit var rvCategories: RecyclerView
    private lateinit var progressBar: LinearLayout
    private lateinit var tvEmpty: TextView
    private lateinit var db: FloraDatabase
    private lateinit var btnOpenList: MaterialButton
    private lateinit var reloadJsonLauncher: ActivityResultLauncher<Intent>
    private lateinit var tvScrollOffsetManager: TvScrollOffsetManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvCategories = findViewById(R.id.rvCategories)
        progressBar = findViewById(R.id.progressBar)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCategories.layoutManager = layoutManager

        tvScrollOffsetManager = TvScrollOffsetManager(this, rvCategories)
        tvScrollOffsetManager.setupTvScrollOffset()

        rvCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    btnOpenList.animate().translationY(0f).setDuration(100).setStartDelay(250).setInterpolator(
                        AccelerateDecelerateInterpolator()).withStartAction {
                        btnOpenList.visibility = View.VISIBLE
                    }.start()
                } else {
                    btnOpenList.animate().translationY(btnOpenList.height.toFloat()).setDuration(100).setStartDelay(250).setInterpolator(
                        AccelerateDecelerateInterpolator()).withEndAction {
                        btnOpenList.visibility = View.GONE
                    }.start()
                }
            }
        })

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

        btnOpenList = findViewById(R.id.btnRefresh)
        btnOpenList.setOnClickListener {
            val intent = Intent(this, JsonManagerActivity::class.java)
            reloadJsonLauncher.launch(intent)
            true
        }

        tvEmpty = findViewById(R.id.tvEmpty)
        db = FloraDatabase.getDatabase(this)

        loadData(false)
    }

    private fun loadData(isSwipe: Boolean) {
        if (!isSwipe) {
            progressBar.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.IO).launch {
            val urls = db.jsonUrlDao().getAll()

            if (urls.isEmpty()) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    tvEmpty.visibility = View.VISIBLE
                    rvCategories.visibility = View.GONE
                    btnOpenList.visibility = View.VISIBLE
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
                            tvScrollOffsetManager.onDataLoaded()
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
