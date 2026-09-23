package pt.tiagoduarte.challenge.form.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pt.tiagoduarte.challenge.form.model.RatingClassification
import pt.tiagoduarte.challenge.form.validation.FormValidator
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(private val clock: Clock) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    // Errors are shown once the user first tries to submit, then kept up to date on every change
    private var showErrors = false

    val today: LocalDate
        get() = LocalDate.now(clock)

    fun onNameChange(name: String) = updateForm { copy(name = name) }

    fun onEmailChange(email: String) = updateForm { copy(email = email) }

    fun onNumberChange(number: String) = updateForm { copy(number = number) }

    fun onPromoCodeChange(promoCode: String) = updateForm { copy(promoCode = promoCode) }

    fun onDeliveryDateChange(deliveryDate: LocalDate) = updateForm { copy(deliveryDate = deliveryDate) }

    fun onRatingChange(rating: RatingClassification) = updateForm { copy(rating = rating) }

    fun onSubmit() {
        val errors = validate(_uiState.value)
        if (errors.hasErrors) {
            showErrors = true
            _uiState.update { it.copy(errors = errors) }
        } else {
            showErrors = false
            _uiState.value = FormUiState(isSubmitted = true)
        }
    }

    fun onSubmittedMessageShown() {
        _uiState.update { it.copy(isSubmitted = false) }
    }

    private fun updateForm(transform: FormUiState.() -> FormUiState) {
        _uiState.update { state ->
            val updated = state.transform()
            if (showErrors) updated.copy(errors = validate(updated)) else updated
        }
    }

    private fun validate(state: FormUiState) = FormErrors(
        name = FormValidator.validateName(state.name),
        email = FormValidator.validateEmail(state.email),
        number = FormValidator.validateNumber(state.number),
        promoCode = FormValidator.validatePromoCode(state.promoCode),
        deliveryDate = FormValidator.validateDeliveryDate(state.deliveryDate, today),
        rating = FormValidator.validateRating(state.rating),
    )
}
