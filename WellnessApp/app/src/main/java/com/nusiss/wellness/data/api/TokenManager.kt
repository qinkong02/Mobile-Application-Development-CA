/**
 * @author WengYuhao
 */
package com.nusiss.wellness.data.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "wellness_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUser(userId: String, userName: String) {
        prefs.edit().putString(KEY_USER_ID, userId).putString(KEY_USER_NAME, userName).apply()
    }

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
