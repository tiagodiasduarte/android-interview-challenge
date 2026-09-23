package pt.tiagoduarte.challenge.data.local.prefs

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AppPreferencesImplTest {

    private fun newPreferences(): AppPreferencesImpl {
        val file = File.createTempFile("test_preferences", ".preferences_pb").apply { deleteOnExit() }
        val dataStore = PreferenceDataStoreFactory.create(produceFile = { file })
        return AppPreferencesImpl(dataStore)
    }

    @Test
    fun `given no value set when isCatalogDownloaded is read then defaults to false`() = runTest {
        // Given
        val preferences = newPreferences()

        // When
        val value = preferences.isCatalogDownloaded.first()

        // Then
        assertFalse(value)
    }

    @Test
    fun `given a value is set when isCatalogDownloaded is read then returns the stored value`() = runTest {
        // Given
        val preferences = newPreferences()

        // When
        preferences.setCatalogDownloaded(true)

        // Then
        assertTrue(preferences.isCatalogDownloaded.first())
    }
}
