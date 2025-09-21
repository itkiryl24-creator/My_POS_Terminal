package com.example.myposterminal.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object TransactionCounter {
    private val Context.dataStore by preferencesDataStore(name = "counter_store")
    private val COUNTER_KEY = intPreferencesKey("transaction_count")

    suspend fun increment(context: Context): Int {
        var newCount = 0
        context.dataStore.edit { prefs ->
            val current = prefs[COUNTER_KEY] ?: 0
            newCount = current + 1
            prefs[COUNTER_KEY] = newCount
        }
        return newCount
    }

    suspend fun reset(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[COUNTER_KEY] = 0
        }
    }
}