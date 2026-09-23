package pt.tiagoduarte.challenge.data.local.db

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchNormalizationTest {

    @Test
    fun `given text with accents and capitals when normalizeForSearch is called then strips them`() {
        // Given
        val text = "Crème Brûlée ÀÉÎÕÜ Ç"

        // When
        val normalized = text.normalizeForSearch()

        // Then
        assertEquals("creme brulee aeiou c", normalized)
    }
}
