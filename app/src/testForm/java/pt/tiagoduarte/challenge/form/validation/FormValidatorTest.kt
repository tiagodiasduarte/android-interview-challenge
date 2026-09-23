package pt.tiagoduarte.challenge.form.validation

import org.junit.Assert.assertEquals
import org.junit.Test
import pt.tiagoduarte.challenge.form.model.RatingClassification
import java.time.LocalDate

class FormValidatorTest {

    @Test
    fun `given a blank name when validateName is called then returns EMPTY`() {
        // Given
        val name = "   "

        // When
        val error = FormValidator.validateName(name)

        // Then
        assertEquals(FormError.EMPTY, error)
    }

    @Test
    fun `given a name when validateName is called then returns no error`() {
        // Given
        val name = "Tiago"

        // When
        val error = FormValidator.validateName(name)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given a blank email when validateEmail is called then returns EMPTY`() {
        // Given
        val email = ""

        // When
        val error = FormValidator.validateEmail(email)

        // Then
        assertEquals(FormError.EMPTY, error)
    }

    @Test
    fun `given an email without a domain when validateEmail is called then returns INVALID_EMAIL`() {
        // Given
        val email = "tiago@"

        // When
        val error = FormValidator.validateEmail(email)

        // Then
        assertEquals(FormError.INVALID_EMAIL, error)
    }

    @Test
    fun `given an email without an at sign when validateEmail is called then returns INVALID_EMAIL`() {
        // Given
        val email = "tiago.example.com"

        // When
        val error = FormValidator.validateEmail(email)

        // Then
        assertEquals(FormError.INVALID_EMAIL, error)
    }

    @Test
    fun `given a valid email when validateEmail is called then returns no error`() {
        // Given
        val email = "tiago.duarte+test@example.co.uk"

        // When
        val error = FormValidator.validateEmail(email)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given a blank number when validateNumber is called then returns EMPTY`() {
        // Given
        val number = ""

        // When
        val error = FormValidator.validateNumber(number)

        // Then
        assertEquals(FormError.EMPTY, error)
    }

    @Test
    fun `given a number with letters when validateNumber is called then returns NOT_DIGITS`() {
        // Given
        val number = "12a4"

        // When
        val error = FormValidator.validateNumber(number)

        // Then
        assertEquals(FormError.NOT_DIGITS, error)
    }

    @Test
    fun `given a number with a sign when validateNumber is called then returns NOT_DIGITS`() {
        // Given
        val number = "-123"

        // When
        val error = FormValidator.validateNumber(number)

        // Then
        assertEquals(FormError.NOT_DIGITS, error)
    }

    @Test
    fun `given only digits when validateNumber is called then returns no error`() {
        // Given
        val number = "0123456789"

        // When
        val error = FormValidator.validateNumber(number)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given a blank promo code when validatePromoCode is called then returns EMPTY`() {
        // Given
        val promoCode = ""

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(FormError.EMPTY, error)
    }

    @Test
    fun `given a lowercase promo code when validatePromoCode is called then returns PROMO_CODE_CHARACTERS`() {
        // Given
        val promoCode = "promo"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(FormError.PROMO_CODE_CHARACTERS, error)
    }

    @Test
    fun `given a promo code with digits when validatePromoCode is called then returns PROMO_CODE_CHARACTERS`() {
        // Given
        val promoCode = "PROMO1"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(FormError.PROMO_CODE_CHARACTERS, error)
    }

    @Test
    fun `given a promo code with accents when validatePromoCode is called then returns PROMO_CODE_CHARACTERS`() {
        // Given
        val promoCode = "PROMOÇÃO"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(FormError.PROMO_CODE_CHARACTERS, error)
    }

    @Test
    fun `given a promo code with 2 characters when validatePromoCode is called then returns PROMO_CODE_LENGTH`() {
        // Given
        val promoCode = "AB"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(FormError.PROMO_CODE_LENGTH, error)
    }

    @Test
    fun `given a promo code with 8 characters when validatePromoCode is called then returns PROMO_CODE_LENGTH`() {
        // Given
        val promoCode = "ABCD-EFG"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(FormError.PROMO_CODE_LENGTH, error)
    }

    @Test
    fun `given a promo code with 3 characters when validatePromoCode is called then returns no error`() {
        // Given
        val promoCode = "A-B"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given a promo code with 7 characters when validatePromoCode is called then returns no error`() {
        // Given
        val promoCode = "SUMMER-"

        // When
        val error = FormValidator.validatePromoCode(promoCode)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given no date when validateDeliveryDate is called then returns EMPTY`() {
        // Given
        val date: LocalDate? = null

        // When
        val error = FormValidator.validateDeliveryDate(date, TODAY)

        // Then
        assertEquals(FormError.EMPTY, error)
    }

    @Test
    fun `given a date after today when validateDeliveryDate is called then returns DATE_IN_FUTURE`() {
        // Given
        val date = TODAY.plusDays(1)

        // When
        val error = FormValidator.validateDeliveryDate(date, TODAY)

        // Then
        assertEquals(FormError.DATE_IN_FUTURE, error)
    }

    @Test
    fun `given a past Monday when validateDeliveryDate is called then returns DATE_ON_MONDAY`() {
        // Given
        val date = LocalDate.of(2026, 9, 21)

        // When
        val error = FormValidator.validateDeliveryDate(date, TODAY)

        // Then
        assertEquals(FormError.DATE_ON_MONDAY, error)
    }

    @Test
    fun `given today when validateDeliveryDate is called then returns no error`() {
        // Given
        val date = TODAY

        // When
        val error = FormValidator.validateDeliveryDate(date, TODAY)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given a past Sunday when validateDeliveryDate is called then returns no error`() {
        // Given
        val date = LocalDate.of(2026, 9, 20)

        // When
        val error = FormValidator.validateDeliveryDate(date, TODAY)

        // Then
        assertEquals(null, error)
    }

    @Test
    fun `given no rating when validateRating is called then returns EMPTY`() {
        // Given
        val rating: RatingClassification? = null

        // When
        val error = FormValidator.validateRating(rating)

        // Then
        assertEquals(FormError.EMPTY, error)
    }

    @Test
    fun `given a rating when validateRating is called then returns no error`() {
        // Given
        val rating = RatingClassification.GOOD

        // When
        val error = FormValidator.validateRating(rating)

        // Then
        assertEquals(null, error)
    }

    private companion object {
        // A Wednesday
        val TODAY: LocalDate = LocalDate.of(2026, 9, 23)
    }
}
