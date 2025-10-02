package com.ch.ktorsample.core.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Utility class for managing authentication tokens and sensitive data
 */
class TokenManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "token_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_USER_ID = "user_id"
        private const val TAG = "TokenManager"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Store an access token
     */
    fun storeAccessToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
        Log.d(TAG, "Access token stored")
    }
    
    /**
     * Store a refresh token
     */
    fun storeRefreshToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_REFRESH_TOKEN, token)
            .apply()
        Log.d(TAG, "Refresh token stored")
    }
    
    /**
     * Store an API key
     */
    fun storeApiKey(key: String) {
        sharedPreferences.edit()
            .putString(KEY_API_KEY, key)
            .apply()
        Log.d(TAG, "API key stored")
    }
    
    /**
     * Store user ID
     */
    fun storeUserId(userId: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_ID, userId)
            .apply()
        Log.d(TAG, "User ID stored")
    }
    
    /**
     * Get access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Get API key
     */
    fun getApiKey(): String? {
        return sharedPreferences.getString(KEY_API_KEY, null)
    }
    
    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    /**
     * Clear all tokens and sensitive data
     */
    fun clearAllTokens() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_API_KEY)
            .remove(KEY_USER_ID)
            .apply()
        Log.d(TAG, "All tokens cleared")
    }
    
    /**
     * Clear specific token
     */
    fun clearAccessToken() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .apply()
        Log.d(TAG, "Access token cleared")
    }
    
    /**
     * Clear refresh token
     */
    fun clearRefreshToken() {
        sharedPreferences.edit()
            .remove(KEY_REFRESH_TOKEN)
            .apply()
        Log.d(TAG, "Refresh token cleared")
    }
    
    /**
     * Clear API key
     */
    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(KEY_API_KEY)
            .apply()
        Log.d(TAG, "API key cleared")
    }
    
    /**
     * Check if any tokens exist
     */
    fun hasTokens(): Boolean {
        return getAccessToken() != null || 
               getRefreshToken() != null || 
               getApiKey() != null || 
               getUserId() != null
    }
    
    /**
     * Get token status for debugging
     */
    fun getTokenStatus(): Map<String, Boolean> {
        return mapOf(
            "access_token" to (getAccessToken() != null),
            "refresh_token" to (getRefreshToken() != null),
            "api_key" to (getApiKey() != null),
            "user_id" to (getUserId() != null)
        )
    }
}
