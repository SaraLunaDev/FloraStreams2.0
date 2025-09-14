package com.example.flora_streams

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flora_streams.adapter.JsonUrlAdapter
import com.example.flora_streams.data.FloraDatabase
import com.example.flora_streams.data.JsonUrlEntity
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JsonManagerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JsonUrlAdapter
    private lateinit var db: FloraDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json_manager)

        val topBarManager: MaterialToolbar = findViewById(R.id.topBarManager)
        setSupportActionBar(topBarManager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        topBarManager.setNavigationOnClickListener { finish() }

        db = FloraDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.rvJsonUrls)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JsonUrlAdapter(mutableListOf(), onEdit = {showEditDialog(it)}, onDelete = {deleteJsonUrl(it)})
        recyclerView.adapter = adapter

        loadUrls()

        topBarManager.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_json_list) {
                showAddDialog()
                true
            } else false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.json_manager_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_json_list -> {
                showAddDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUrls() {
        CoroutineScope(Dispatchers.IO).launch {
            val urls = db.jsonUrlDao().getAll()
            runOnUiThread { adapter.updateList(urls) }
        }
    }

    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_edit_json, null)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)

        AlertDialog.Builder(this).setTitle(R.string.add_json).setView(view).setPositiveButton(R.string.save) { _, _ ->
            val name = etName.text.toString()
            val url = etUrl.text.toString()
            if (name.isNotBlank() && url.isNotBlank()) addJsonUrl(name, url)
        }
            .setNegativeButton(R.string.cancel, null).show()
    }

    private fun showEditDialog(item: JsonUrlEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_add_edit_json, null)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)

        etName.setText(item.name)
        etUrl.setText(item.url)

        AlertDialog.Builder(this).setTitle(R.string.edit_json).setView(view).setPositiveButton(R.string.save) { _, _ ->
            val name = etName.text.toString()
            val url = etUrl.text.toString()
            if (name.isNotBlank() && url.isNotBlank()) updateJsonUrl(item.copy(name = name, url = url))
        }
            .setNegativeButton(R.string.cancel, null).show()
    }

    private fun addJsonUrl(name: String, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.jsonUrlDao().insert(JsonUrlEntity(name = name, url = url))
            loadUrls()
        }
    }

    private fun updateJsonUrl(item: JsonUrlEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            db.jsonUrlDao().update(item)
            loadUrls()
        }
    }

    private fun deleteJsonUrl(item: JsonUrlEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            db.jsonUrlDao().delete(item)
            loadUrls()
        }
    }
}