package com.example.imageEditor.encrypPreference

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences

class MyEncryptPreference(val context: Context) {
    private val preference = EncryptedSharedPreferences.create(
        "preference", "keyAlias",
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun saveData(key: String, value: String) {
        preference.edit().putString(key, value).apply()
    }

    fun getData(key: String): String? {
        return preference.getString(key, null)
    }
}
