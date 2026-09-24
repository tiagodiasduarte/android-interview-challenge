package pt.tiagoduarte.challenge.form.validation

import pt.tiagoduarte.challenge.form.model.RatingClassification
import java.time.DayOfWeek
import java.time.LocalDate

object FormValidator {

    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    private val DIGITS_REGEX = "^\\d+$".toRegex()
    private val PROMO_CODE_REGEX = "^[A-Z-]+$".toRegex()
    private val PROMO_CODE_LENGTH = 3..7

    fun validateName(name: String): FormError? =
        if (name.isBlank()) FormError.EMPTY else null

    fun validateEmail(email: String): FormError? = when {
        email.isBlank() -> FormError.EMPTY
        !EMAIL_REGEX.matches(email.trim()) -> FormError.INVALID_EMAIL
        else -> null
    }

    fun validateNumber(number: String): FormError? = when {
        number.isBlank() -> FormError.EMPTY
        !DIGITS_REGEX.matches(number) -> FormError.NOT_DIGITS
        else -> null
    }

    fun validatePromoCode(promoCode: String): FormError? = when {
        promoCode.isBlank() -> FormError.EMPTY
        !PROMO_CODE_REGEX.matches(promoCode) -> FormError.PROMO_CODE_CHARACTERS
        promoCode.length !in PROMO_CODE_LENGTH -> FormError.PROMO_CODE_LENGTH
        else -> null
    }

    fun validateDeliveryDate(date: LocalDate?, today: LocalDate): FormError? = when {
        date == null -> FormError.EMPTY
        date.isAfter(today) -> FormError.DATE_IN_FUTURE
        date.dayOfWeek == DayOfWeek.MONDAY -> FormError.DATE_ON_MONDAY
        else -> null
    }

    fun validateRating(rating: RatingClassification?): FormError? =
        if (rating == null) FormError.EMPTY else null
}
