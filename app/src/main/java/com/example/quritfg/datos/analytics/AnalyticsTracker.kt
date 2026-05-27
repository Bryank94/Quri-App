package com.example.quritfg.datos.analytics

interface AnalyticsTracker {
    fun track(event: String, properties: Map<String, String> = emptyMap())
}
