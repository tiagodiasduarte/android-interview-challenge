package pt.tiagoduarte.challenge.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
) : AppPreferences {

    override val isCatalogDownloaded: Flow<Boolean> =
        dataStore.data.map { it[CATALOG_DOWNLOADED_KEY] ?: false }

    override suspend fun setCatalogDownloaded(value: Boolean) {
        dataStore.edit { it[CATALOG_DOWNLOADED_KEY] = value }
    }

    private companion object {
        val CATALOG_DOWNLOADED_KEY = booleanPreferencesKey("catalog_downloaded")
    }
}
