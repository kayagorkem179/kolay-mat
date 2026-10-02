package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("tr", "TR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.getDefault()
                }
                isInitialized = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }
                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            } else {
                Log.e("TtsManager", "TTS initialization failed")
            }
        }
    }

    fun speak(text: String, speechRate: Float = 1.0f) {
        if (!isInitialized || tts == null) return

        stop()
        tts?.setSpeechRate(speechRate)

        // Convert common mathematical signs to clear Turkish spoken words
        val formatted = text
            .replace("+", " artı ")
            .replace("-", " eksi ")
            .replace("*", " çarpı ")
            .replace("/", " bölü ")
            .replace("=", " eşittir ")
            .replace("^2", " kare ")
            .replace("^3", " küp ")
            .replace("√", " karekök ")
            .replace("≤", " küçük eşittir ")
            .replace("≥", " büyük eşittir ")
            .replace("≠", " eşit değildir ")

        tts?.speak(formatted, TextToSpeech.QUEUE_FLUSH, null, "KolayMat_Utterance")
        _isSpeaking.value = true
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
