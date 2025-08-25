package com.adm.weatherapp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adm.weatherapp.service.DataStoreAPI
import com.adm.weatherapp.util.Constants.DATASTORE_NAME
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

@Singleton
class DataStoreRepository @Inject constructor(
    private val context: Context
) : DataStoreAPI {
    override suspend fun putString(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    override suspend fun getString(key: String): String? {
        return try {
            val dataStoreKey = stringPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[dataStoreKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    override suspend fun getInt(key: String): Int? {
        return try {
            val dataStoreKey = intPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[dataStoreKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }

    override suspend fun getBoolean(key: String): Boolean? {
        return try {
            val dataStoreKey = booleanPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[dataStoreKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun clear(key: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            if (preferences.contains(dataStoreKey)) {
                preferences.remove(dataStoreKey)
            }
        }
    }
}