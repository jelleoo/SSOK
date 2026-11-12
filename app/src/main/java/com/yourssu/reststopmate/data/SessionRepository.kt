package com.yourssu.reststopmate.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// One DataStore instance per file, shared across repository instances.
private val Context.sessionDataStore by preferencesDataStore(name = "guest_session")

class SessionRepository(context: Context) {
    private val store = context.applicationContext.sessionDataStore
    private val guestKey = booleanPreferencesKey("guest_started")

    // Propagate read errors: a storage failure must not silently log the user out.
    val isGuest: Flow<Boolean> = store.data.map { it[guestKey] ?: false }

    suspend fun startGuest() {
        store.edit { it[guestKey] = true }
    }
}
