package pt.tiagoduarte.challenge.form.presentation

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.tiagoduarte.challenge.form.model.RatingClassification
import pt.tiagoduarte.challenge.form.validation.FormError

class FormStringsTest {

    @Test
    fun `given every rating when labelRes is called then each has its own label`() {
        // Given
        val ratings = RatingClassification.entries

        // When
        val labels = ratings.map { it.labelRes() }

        // Then
        assertEquals(ratings.size, labels.toSet().size)
    }

    @Test
    fun `given every form error when messageRes is called then each has its own message`() {
        // Given
        val errors = FormError.entries

        // When
        val messages = errors.map { it.messageRes() }

        // Then
        assertEquals(errors.size, messages.toSet().size)
    }
}
