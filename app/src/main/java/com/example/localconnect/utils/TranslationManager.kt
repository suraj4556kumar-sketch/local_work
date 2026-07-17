package com.example.localconnect.utils

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslationManager {
    private const val TAG = "TranslationManager"
    private val translators = mutableMapOf<String, Translator>()

    /**
     * Translates the given [text] to the [targetLanguage] code.
     * Assumes the source language is English ("en").
     * If the target language is "en" or translation fails, returns the original text.
     */
    fun translate(
        text: String,
        targetLanguage: String,
        onResult: (String) -> Unit
    ) {
        if (text.isBlank() || targetLanguage == "en") {
            onResult(text)
            return
        }

        val targetLangCode = getMLKitLanguageCode(targetLanguage)
        if (targetLangCode == null) {
            onResult(text)
            return
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLangCode)
            .build()

        val translator = translators.getOrPut(targetLanguage) {
            Translation.getClient(options)
        }

        val defaultConditions = DownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(defaultConditions)
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        onResult(translatedText)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Translation failed", e)
                        onResult(text)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Model download failed", e)
                onResult(text)
            }
    }

    private fun getMLKitLanguageCode(appLangCode: String): String? {
        return when (appLangCode) {
            "hi" -> TranslateLanguage.HINDI
            "bn" -> TranslateLanguage.BENGALI
            else -> null
        }
    }
}
