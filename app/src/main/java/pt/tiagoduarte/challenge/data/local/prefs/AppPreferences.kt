package pt.tiagoduarte.challenge.data.local.prefs

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val isCatalogDownloaded: Flow<Boolean>
    suspend fun setCatalogDownloaded(value: Boolean)
}

