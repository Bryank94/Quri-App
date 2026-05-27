package com.example.quritfg.datos.analytics

class PostHogAnalyticsTracker : AnalyticsTracker {
    override fun track(event: String, properties: Map<String, String>) {
        // Preparado para conectar el SDK de PostHog cuando exista API key.
    }
}
