package pt.tiagoduarte.challenge.listing.presentation.products

import org.junit.Assert.assertEquals
import org.junit.Test

class RatingIconTest {

    @Test
    fun `given a rating below 3 when ratingCategoryOf is called then returns LOW`() {
        // Given
        val rating = 2.99

        // When
        val category = ratingCategoryOf(rating)

        // Then
        assertEquals(RatingCategory.LOW, category)
    }

    @Test
    fun `given a rating of exactly 3 when ratingCategoryOf is called then returns MEDIUM`() {
        // Given
        val rating = 3.0

        // When
        val category = ratingCategoryOf(rating)

        // Then
        assertEquals(RatingCategory.MEDIUM, category)
    }

    @Test
    fun `given a rating of exactly 4 when ratingCategoryOf is called then returns MEDIUM`() {
        // Given
        val rating = 4.0

        // When
        val category = ratingCategoryOf(rating)

        // Then
        assertEquals(RatingCategory.MEDIUM, category)
    }

    @Test
    fun `given a rating above 4 when ratingCategoryOf is called then returns HIGH`() {
        // Given
        val rating = 4.01

        // When
        val category = ratingCategoryOf(rating)

        // Then
        assertEquals(RatingCategory.HIGH, category)
    }
}
