package pt.tiagoduarte.challenge.form.presentation

import androidx.annotation.StringRes
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.form.model.RatingClassification
import pt.tiagoduarte.challenge.form.validation.FormError

@StringRes
fun RatingClassification.labelRes(): Int = when (this) {
    RatingClassification.BAD -> R.string.form_rating_bad
    RatingClassification.SATISFACTORY -> R.string.form_rating_satisfactory
    RatingClassification.GOOD -> R.string.form_rating_good
    RatingClassification.VERY_GOOD -> R.string.form_rating_very_good
    RatingClassification.EXCELLENT -> R.string.form_rating_excellent
}

@StringRes
fun FormError.messageRes(): Int = when (this) {
    FormError.EMPTY -> R.string.form_error_empty
    FormError.INVALID_EMAIL -> R.string.form_error_email
    FormError.NOT_DIGITS -> R.string.form_error_digits
    FormError.PROMO_CODE_CHARACTERS -> R.string.form_error_promo_code_characters
    FormError.PROMO_CODE_LENGTH -> R.string.form_error_promo_code_length
    FormError.DATE_ON_MONDAY -> R.string.form_error_date_monday
    FormError.DATE_IN_FUTURE -> R.string.form_error_date_future
}
