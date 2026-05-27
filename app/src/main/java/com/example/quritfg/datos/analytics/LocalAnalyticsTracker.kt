package com.example.quritfg.datos.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

class LocalAnalyticsTracker(context: Context) : AnalyticsTracker {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("quri_analytics", Context.MODE_PRIVATE)
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(appContext)

    override fun track(event: String, properties: Map<String, String>) {
        val countKey = "count_$event"
        val lastKey = "last_$event"
        val count = prefs.getInt(countKey, 0) + 1

        prefs.edit()
            .putInt(countKey, count)
            .putLong(lastKey, System.currentTimeMillis())
            .putString("last_properties_$event", properties.entries.joinToString { "${it.key}=${it.value}" })
            .apply()

        firebaseAnalytics.logEvent(event.normalizarEventoFirebase(), properties.toFirebaseBundle())
        Log.d("QuriAnalytics", "$event #$count $properties")
    }
}

private fun Map<String, String>.toFirebaseBundle(): Bundle =
    Bundle().apply {
        entries.forEach { (key, value) ->
            putString(key.normalizarParametroFirebase(), value.take(100))
        }
    }

private fun String.normalizarEventoFirebase(): String =
    normalizarFirebase(maxLength = 40)

private fun String.normalizarParametroFirebase(): String =
    normalizarFirebase(maxLength = 40)

private fun String.normalizarFirebase(maxLength: Int): String {
    val normalized = lowercase()
        .replace(Regex("[^a-z0-9_]+"), "_")
        .trim('_')
        .ifBlank { "quri_event" }
    val startsWithLetter = normalized.firstOrNull()?.isLetter() == true
    val safe = if (startsWithLetter) normalized else "quri_$normalized"
    return safe.take(maxLength)
}
