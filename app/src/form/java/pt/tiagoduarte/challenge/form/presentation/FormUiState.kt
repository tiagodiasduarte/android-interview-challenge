package pt.tiagoduarte.challenge.form.presentation

import pt.tiagoduarte.challenge.form.model.RatingClassification
import pt.tiagoduarte.challenge.form.validation.FormError
import java.time.LocalDate

data class FormUiState(
    val name: String = "",
    val email: String = "",
    val number: String = "",
    val promoCode: String = "",
    val deliveryDate: LocalDate? = null,
    val rating: RatingClassification? = null,
    val errors: FormErrors = FormErrors(),
    val isSubmitted: Boolean = false,
)

data class FormErrors(
    val name: FormError? = null,
    val email: FormError? = null,
    val number: FormError? = null,
    val promoCode: FormError? = null,
    val deliveryDate: FormError? = null,
    val rating: FormError? = null,
) {
    val hasErrors: Boolean
        get() = listOfNotNull(name, email, number, promoCode, deliveryDate, rating).isNotEmpty()
}
