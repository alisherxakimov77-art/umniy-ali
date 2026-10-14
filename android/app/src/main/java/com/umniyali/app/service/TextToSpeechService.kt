package com.umniyali.app.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

class TextToSpeechService(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private val pendingQueue = mutableListOf<Pair<String, () -> Unit>>()

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isReady = true
                setLanguage("ru-RU")
                drainQueue()
            }
        }
    }

    fun setLanguage(localeTag: String) {
        val locale = Locale.forLanguageTag(localeTag)
        tts?.setLanguage(locale)
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        val utteranceId = UUID.randomUUID().toString()
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                onDone?.invoke()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {}
        })

        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            pendingQueue.add(text to { onDone?.invoke() })
        }
    }

    private fun drainQueue() {
        pendingQueue.forEach { (text, onDone) ->
            speak(text) { onDone() }
        }
        pendingQueue.clear()
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
