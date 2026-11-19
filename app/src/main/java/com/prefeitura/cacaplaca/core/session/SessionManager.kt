package com.prefeitura.cacaplaca.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _sessionExpiredFlow = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredFlow: kotlinx.coroutines.flow.SharedFlow<Unit> = _sessionExpiredFlow

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).commit()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).commit()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun saveUserName(userName: String) {
        prefs.edit().putString(KEY_USER_NAME, userName).commit()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }

    fun clearSession() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_REFRESH_TOKEN).remove(KEY_USER_NAME).commit()
        _sessionExpiredFlow.tryEmit(Unit)
    }

    fun saveBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, url).commit()
    }

    fun getBaseUrl(): String? {
        return prefs.getString(KEY_BASE_URL, null)
    }

    companion object {
        private const val PREF_NAME = "cacaplaca_secure_session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_BASE_URL = "custom_base_url"
    }
}
