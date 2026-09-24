package pt.tiagoduarte.challenge.form.presentation

import androidx.lifecycle.SavedStateHandle
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
class FormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val clock: Clock,
) : ViewModel() {

    // Errors are shown once the user first tries to submit, then kept up to date on every change
    private var showErrors: Boolean = savedStateHandle[KEY_SHOW_ERRORS] ?: false

    private val _uiState = MutableStateFlow(restoreForm())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

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
        saveForm(_uiState.value)
    }

    fun onSubmittedMessageShown() {
        _uiState.update { it.copy(isSubmitted = false) }
    }

    private fun updateForm(transform: FormUiState.() -> FormUiState) {
        _uiState.update { state ->
            val updated = state.transform()
            if (showErrors) updated.copy(errors = validate(updated)) else updated
        }
        saveForm(_uiState.value)
    }

    // Keeps what the user typed across process death; errors are recomputed from it
    private fun saveForm(state: FormUiState) {
        savedStateHandle[KEY_NAME] = state.name
        savedStateHandle[KEY_EMAIL] = state.email
        savedStateHandle[KEY_NUMBER] = state.number
        savedStateHandle[KEY_PROMO_CODE] = state.promoCode
        savedStateHandle[KEY_DELIVERY_DATE] = state.deliveryDate?.toEpochDay()
        savedStateHandle[KEY_RATING] = state.rating?.name
        savedStateHandle[KEY_SHOW_ERRORS] = showErrors
    }

    private fun restoreForm(): FormUiState {
        val form = FormUiState(
            name = savedStateHandle[KEY_NAME] ?: "",
            email = savedStateHandle[KEY_EMAIL] ?: "",
            number = savedStateHandle[KEY_NUMBER] ?: "",
            promoCode = savedStateHandle[KEY_PROMO_CODE] ?: "",
            deliveryDate = savedStateHandle.get<Long>(KEY_DELIVERY_DATE)?.let(LocalDate::ofEpochDay),
            rating = savedStateHandle.get<String>(KEY_RATING)?.let(RatingClassification::valueOf),
        )
        return if (showErrors) form.copy(errors = validate(form)) else form
    }

    private fun validate(state: FormUiState) = FormErrors(
        name = FormValidator.validateName(state.name),
        email = FormValidator.validateEmail(state.email),
        number = FormValidator.validateNumber(state.number),
        promoCode = FormValidator.validatePromoCode(state.promoCode),
        deliveryDate = FormValidator.validateDeliveryDate(state.deliveryDate, today),
        rating = FormValidator.validateRating(state.rating),
    )

    private companion object {
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_NUMBER = "number"
        const val KEY_PROMO_CODE = "promoCode"
        const val KEY_DELIVERY_DATE = "deliveryDate"
        const val KEY_RATING = "rating"
        const val KEY_SHOW_ERRORS = "showErrors"
    }
}
