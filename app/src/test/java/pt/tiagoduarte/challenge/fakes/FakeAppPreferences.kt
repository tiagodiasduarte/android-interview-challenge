package pt.tiagoduarte.challenge.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.tiagoduarte.challenge.data.local.prefs.AppPreferences

class FakeAppPreferences(initialValue: Boolean = false) : AppPreferences {

    private val catalogDownloaded = MutableStateFlow(initialValue)

    override val isCatalogDownloaded: Flow<Boolean> = catalogDownloaded

    override suspend fun setCatalogDownloaded(value: Boolean) {
        catalogDownloaded.value = value
    }
}
