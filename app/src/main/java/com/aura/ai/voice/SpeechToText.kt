package com.aura.ai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/** One emission per recognition lifecycle event, consumed by the UI. */
sealed interface SpeechEvent {
    data object ReadyForSpeech : SpeechEvent
    data object BeginningOfSpeech : SpeechEvent
    data class Partial(val text: String) : SpeechEvent
    data class Final(val text: String) : SpeechEvent
    data class Error(val message: String) : SpeechEvent
    data object EndOfSpeech : SpeechEvent
}

/**
 * Wraps Android's on-device-capable [SpeechRecognizer] as a cold Flow. Recognition
 * uses the platform recognizer, which on modern devices runs locally; no audio is
 * sent to any T1000-controlled server.
 */
@Singleton
class SpeechToText @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun listen(): Flow<SpeechEvent> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(SpeechEvent.Error("Speech recognition is not available on this device."))
            close()
            return@callbackFlow
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { trySend(SpeechEvent.ReadyForSpeech) }
            override fun onBeginningOfSpeech() { trySend(SpeechEvent.BeginningOfSpeech) }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { trySend(SpeechEvent.EndOfSpeech) }

            override fun onError(error: Int) {
                trySend(SpeechEvent.Error(errorMessage(error)))
                close()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.let { trySend(SpeechEvent.Partial(it)) }
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                trySend(SpeechEvent.Final(text))
                close()
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)

        awaitClose {
            recognizer.stopListening()
            recognizer.destroy()
        }
    }

    private fun errorMessage(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
        SpeechRecognizer.ERROR_CLIENT -> "Recognizer client error."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
        SpeechRecognizer.ERROR_NETWORK -> "Network error (offline recognition unavailable for this language)."
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout."
        SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that — try again."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy."
        SpeechRecognizer.ERROR_SERVER -> "Recognition server error."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
        else -> "Speech recognition error ($code)."
    }
}
