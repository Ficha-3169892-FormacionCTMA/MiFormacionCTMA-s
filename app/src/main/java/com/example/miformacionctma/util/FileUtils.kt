package com.example.miformacionctma.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    fun createUri(context: Context): Uri {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "EVIDENCIA_$timestamp.jpg"
        val storageDir = File(context.filesDir, "evidencias").apply {
            if (!exists()) mkdirs()
        }
        val file = File(storageDir, fileName)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getMetadata(context: Context, uri: Uri): Pair<String, Long> {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val size = contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
        return mimeType to size
    }
}
