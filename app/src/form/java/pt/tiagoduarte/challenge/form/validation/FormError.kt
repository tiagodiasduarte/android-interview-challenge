package pt.tiagoduarte.challenge.form.validation

enum class FormError {
    EMPTY,
    INVALID_EMAIL,
    NOT_DIGITS,
    PROMO_CODE_CHARACTERS,
    PROMO_CODE_LENGTH,
    DATE_ON_MONDAY,
    DATE_IN_FUTURE,
}
