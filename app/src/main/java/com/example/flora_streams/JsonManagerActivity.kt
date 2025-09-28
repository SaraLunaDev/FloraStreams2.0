package com.example.flora_streams

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flora_streams.adapter.JsonUrlAdapter
import com.example.flora_streams.data.FloraDatabase
import com.example.flora_streams.data.JsonUrlEntity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JsonManagerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JsonUrlAdapter
    private lateinit var db: FloraDatabase
    private lateinit var btnAddList: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json_manager)

        db = FloraDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.rvJsonUrls)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JsonUrlAdapter(mutableListOf(), onEdit = {showEditDialog(it)}, onDelete = {deleteJsonUrl(it)})
        recyclerView.adapter = adapter

        loadUrls()

        btnAddList = findViewById(R.id.btnAddList)
        btnAddList.setOnClickListener {
            showAddDialog()
            true
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

        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        tvDialogTitle.setText(R.string.add_json)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val url = etUrl.text.toString()
            if (name.isNotBlank() && url.isNotBlank()) {
                addJsonUrl(name, url)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showEditDialog(item: JsonUrlEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_add_edit_json, null)

        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        tvDialogTitle.setText(R.string.edit_json)
        etName.setText(item.name)
        etUrl.setText(item.url)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val url = etUrl.text.toString()
            if (name.isNotBlank() && url.isNotBlank()) {
                updateJsonUrl(item.copy(name = name, url = url))
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
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