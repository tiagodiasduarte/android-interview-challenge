package pt.tiagoduarte.challenge.form.presentation

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import pt.tiagoduarte.challenge.form.model.RatingClassification
import pt.tiagoduarte.challenge.form.validation.FormError
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

class FormViewModelTest {

    // A Wednesday
    private val today = LocalDate.of(2026, 9, 23)
    private val clock = Clock.fixed(today.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC)

    @Test
    fun `given invalid fields when they change before submitting then no errors are shown`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock)

        // When
        viewModel.onEmailChange("tiago@")

        // Then
        assertEquals(FormErrors(), viewModel.uiState.value.errors)
    }

    @Test
    fun `given an empty form when onSubmit is called then every field shows EMPTY`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock)

        // When
        viewModel.onSubmit()

        // Then
        val empty = FormError.EMPTY
        assertEquals(FormErrors(empty, empty, empty, empty, empty, empty), viewModel.uiState.value.errors)
        assertFalse(viewModel.uiState.value.isSubmitted)
    }

    @Test
    fun `given a failed submit when a field is fixed then its error clears right away`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock)
        viewModel.onSubmit()

        // When
        viewModel.onNameChange("Tiago")

        // Then
        assertEquals(null, viewModel.uiState.value.errors.name)
        assertEquals(FormError.EMPTY, viewModel.uiState.value.errors.email)
    }

    @Test
    fun `given a failed submit when a field becomes invalid then its error updates right away`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock)
        viewModel.onSubmit()

        // When
        viewModel.onPromoCodeChange("promo")

        // Then
        assertEquals(FormError.PROMO_CODE_CHARACTERS, viewModel.uiState.value.errors.promoCode)
    }

    @Test
    fun `given a date after the clock's today when onSubmit is called then the date shows DATE_IN_FUTURE`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock).apply { fillValidForm() }
        viewModel.onDeliveryDateChange(today.plusDays(1))

        // When
        viewModel.onSubmit()

        // Then
        assertEquals(FormError.DATE_IN_FUTURE, viewModel.uiState.value.errors.deliveryDate)
        assertFalse(viewModel.uiState.value.isSubmitted)
    }

    @Test
    fun `given a valid form when onSubmit is called then it is submitted and cleared`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock).apply { fillValidForm() }

        // When
        viewModel.onSubmit()

        // Then
        assertEquals(FormUiState(isSubmitted = true), viewModel.uiState.value)
    }

    @Test
    fun `given a submitted form when onSubmittedMessageShown is called then isSubmitted resets`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock).apply { fillValidForm() }
        viewModel.onSubmit()

        // When
        viewModel.onSubmittedMessageShown()

        // Then
        assertFalse(viewModel.uiState.value.isSubmitted)
    }

    @Test
    fun `given a successful submit when a field changes then no errors are shown`() {
        // Given
        val viewModel = FormViewModel(SavedStateHandle(), clock).apply { fillValidForm() }
        viewModel.onSubmit()

        // When
        viewModel.onEmailChange("tiago@")

        // Then
        assertFalse(viewModel.uiState.value.errors.hasErrors)
    }

    @Test
    fun `given a filled form when the ViewModel is recreated from its saved state then restores every field`() {
        // Given
        val savedStateHandle = SavedStateHandle()
        val viewModel = FormViewModel(savedStateHandle, clock).apply { fillValidForm() }

        // When
        val restored = FormViewModel(savedStateHandle, clock)

        // Then
        assertEquals(viewModel.uiState.value, restored.uiState.value)
    }

    @Test
    fun `given a failed submit when the ViewModel is recreated from its saved state then still shows the errors`() {
        // Given
        val savedStateHandle = SavedStateHandle()
        FormViewModel(savedStateHandle, clock).apply {
            onEmailChange("tiago@")
            onSubmit()
        }

        // When
        val restored = FormViewModel(savedStateHandle, clock)

        // Then
        assertEquals(FormError.INVALID_EMAIL, restored.uiState.value.errors.email)
        assertEquals(FormError.EMPTY, restored.uiState.value.errors.name)
    }

    @Test
    fun `given a submitted form when the ViewModel is recreated from its saved state then starts empty`() {
        // Given
        val savedStateHandle = SavedStateHandle()
        FormViewModel(savedStateHandle, clock).apply {
            fillValidForm()
            onSubmit()
        }

        // When
        val restored = FormViewModel(savedStateHandle, clock)

        // Then
        assertEquals(FormUiState(), restored.uiState.value)
    }

    private fun FormViewModel.fillValidForm() {
        onNameChange("Tiago")
        onEmailChange("tiago@example.com")
        onNumberChange("912345678")
        onPromoCodeChange("PROMO-A")
        onDeliveryDateChange(today.minusDays(1))
        onRatingChange(RatingClassification.VERY_GOOD)
    }
}
