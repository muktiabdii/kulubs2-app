package com.example.kulubs.util

import android.content.Context
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

object CloudinaryConfig {
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            try {
                val config = mapOf(
                    "cloud_name" to "dvwbrl4el",
                    "api_key" to "534342963727385",
                    "api_secret" to "9i2Hip0nzshMbu6rWqSgSXQG91Q"
                )
                MediaManager.init(context, config)
                isInitialized = true
            } catch (e: Exception) {
                isInitialized = false
                throw CloudinaryInitializationException("Failed to initialize Cloudinary: ${e.message}")
            }
        }
    }

    fun getMediaManager(): MediaManager {
        if (!isInitialized) {
            throw CloudinaryNotInitializedException("Cloudinary has not been initialized. Call init() first.")
        }
        return MediaManager.get()
    }

    fun uploadImage(filePath: String, callback: (String?, String?) -> Unit) {
        try {
            getMediaManager().upload(filePath)
                .unsigned("preset_kulubs") // Menggunakan upload preset yang benar
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["url"] as String?
                        callback(url, null)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        callback(null, error.description)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                    }
                })
                .dispatch()
        } catch (e: Exception) {
            callback(null, "Gagal memulai unggahan: ${e.message}")
        }
    }
}

class CloudinaryInitializationException(message: String) : Exception(message)
class CloudinaryNotInitializedException(message: String) : Exception(message)