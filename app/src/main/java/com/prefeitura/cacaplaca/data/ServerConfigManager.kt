package com.prefeitura.cacaplaca.data

import android.content.Context
import android.content.SharedPreferences

class ServerConfigManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("server_prefs", Context.MODE_PRIVATE)

    fun getBaseUrl(): String {
        return prefs.getString("base_url", "http://192.168.0.6:8000/") ?: "http://192.168.0.6:8000/"
    }

    fun setBaseUrl(input: String) {
        val formattedUrl = formatUrl(input)
        prefs.edit().putString("base_url", formattedUrl).apply()
    }

    private fun formatUrl(input: String): String {
        var url = input.trim()
        if (url.isEmpty()) return "http://192.168.0.6:8000/"

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        val withoutScheme = url.substringAfter("://")
        val hostPart = withoutScheme.substringBefore("/")
        if (!hostPart.contains(":")) {
            url = url.replaceFirst(hostPart, "$hostPart:8000")
        }

        if (!url.endsWith("/")) {
            url = "$url/"
        }
        return url
    }
}
