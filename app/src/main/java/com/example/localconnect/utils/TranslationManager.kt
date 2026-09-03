package com.example.localconnect.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

object TranslationManager {
    private const val TAG = "TranslationManager"
    private const val API_KEY = "AIzaSyCAnoBKHOcmB4GyOlAn-Vd9pvUeY8n3XpY" // Your Google Cloud API Key
    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // In-memory cache so we don't hit Firestore for the same string twice in one session
    private val memoryCache = java.util.concurrent.ConcurrentHashMap<String, String>()

    fun translate(
        text: String,
        targetLanguage: String,
        onResult: (String) -> Unit
    ) {
        if (text.isBlank()) {
            onResult(text)
            return
        }

        val cacheKey = "$targetLanguage:${hashString(text)}"
        
        // 1. Check in-memory cache
        if (memoryCache.containsKey(cacheKey)) {
            onResult(memoryCache[cacheKey]!!)
            return
        }

        scope.launch {
            try {
                // 2. Check Firestore Cache
                val docRef = db.collection("translations").document(cacheKey)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    val translated = snapshot.getString("translatedText")
                    if (!translated.isNullOrBlank()) {
                        memoryCache[cacheKey] = translated
                        withContext(Dispatchers.Main) { onResult(translated) }
                        return@launch
                    }
                }

                // 3. Call Google Cloud Translation API
                val translated = fetchFromGoogleCloud(text, targetLanguage)
                if (translated != null) {
                    // Save to memory
                    memoryCache[cacheKey] = translated
                    // Save to Firestore
                    docRef.set(mapOf(
                        "originalText" to text,
                        "targetLanguage" to targetLanguage,
                        "translatedText" to translated
                    ))
                    withContext(Dispatchers.Main) { onResult(translated) }
                } else {
                    withContext(Dispatchers.Main) { onResult(text) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Translation failed", e)
                withContext(Dispatchers.Main) { onResult(text) }
            }
        }
    }

    private fun fetchFromGoogleCloud(text: String, targetLanguage: String): String? {
        try {
            val urlString = "https://translation.googleapis.com/language/translate/v2?key=$API_KEY"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.doOutput = true

            val jsonInputString = JSONObject().apply {
                put("q", text)
                put("target", targetLanguage)
                put("format", "text")
            }.toString()

            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                val jsonObject = JSONObject(response)
                val translations = jsonObject.getJSONObject("data").getJSONArray("translations")
                if (translations.length() > 0) {
                    return translations.getJSONObject(0).getString("translatedText")
                }
            } else {
                val error = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                Log.e(TAG, "Cloud API Error: $responseCode - $error")
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchFromGoogleCloud exception", e)
        }
        return null
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

