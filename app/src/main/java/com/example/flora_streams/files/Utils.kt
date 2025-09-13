package com.example.flora_streams.files

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

fun openAceStream(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No se detectó AceStream",
            Toast.LENGTH_SHORT
        ).show()
    }
}