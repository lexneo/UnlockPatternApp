package com.lexneoapps.unlockpatternapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lexneoapps.unlockpatternapp.data.PreferencesManager.PreferencesKeys.TAP_PASSWORD_STRING
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.prefDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")



@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.prefDataStore


    val password: Flow<String>
        get() = dataStore.data.map { preferences ->
            val password = preferences[TAP_PASSWORD_STRING] ?: ""
            password
        }

    suspend fun updatePassword(string: String) {
        dataStore.edit { preferences ->
            preferences[TAP_PASSWORD_STRING] = string
        }
    }



    private object PreferencesKeys {

        val TAP_PASSWORD_STRING = stringPreferencesKey("TAP_PASSWORD_STRING")

    }
}