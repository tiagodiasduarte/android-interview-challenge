package pt.tiagoduarte.challenge.form.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DeliverySelectableDatesTest {

    // A Wednesday
    private val today = LocalDate.of(2026, 9, 23)
    private val selectableDates = DeliverySelectableDates(today)

    @Test
    fun `given a past day that is not a Monday when isSelectableDate is called then returns true`() {
        // Given
        val date = LocalDate.of(2026, 9, 20)

        // When
        val selectable = selectableDates.isSelectableDate(date.toUtcMillis())

        // Then
        assertTrue(selectable)
    }

    @Test
    fun `given a Monday when isSelectableDate is called then returns false`() {
        // Given
        val date = LocalDate.of(2026, 9, 21)

        // When
        val selectable = selectableDates.isSelectableDate(date.toUtcMillis())

        // Then
        assertFalse(selectable)
    }

    @Test
    fun `given a future day when isSelectableDate is called then returns false`() {
        // Given
        val date = today.plusDays(1)

        // When
        val selectable = selectableDates.isSelectableDate(date.toUtcMillis())

        // Then
        assertFalse(selectable)
    }

    @Test
    fun `given the current year when isSelectableYear is called then returns true`() {
        // Given
        val year = today.year

        // When
        val selectable = selectableDates.isSelectableYear(year)

        // Then
        assertTrue(selectable)
    }

    @Test
    fun `given a future year when isSelectableYear is called then returns false`() {
        // Given
        val year = today.year + 1

        // When
        val selectable = selectableDates.isSelectableYear(year)

        // Then
        assertFalse(selectable)
    }

    @Test
    fun `given a date when converted to UTC millis and back then returns the same date`() {
        // Given
        val date = LocalDate.of(2026, 2, 28)

        // When
        val converted = date.toUtcMillis().toLocalDate()

        // Then
        assertEquals(date, converted)
    }
}
