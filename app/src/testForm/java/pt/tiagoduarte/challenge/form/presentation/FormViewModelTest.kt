package pt.tiagoduarte.challenge.form.presentation

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
        val viewModel = FormViewModel(clock)

        // When
        viewModel.onEmailChange("tiago@")

        // Then
        assertEquals(FormErrors(), viewModel.uiState.value.errors)
    }

    @Test
    fun `given an empty form when onSubmit is called then every field shows EMPTY`() {
        // Given
        val viewModel = FormViewModel(clock)

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
        val viewModel = FormViewModel(clock)
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
        val viewModel = FormViewModel(clock)
        viewModel.onSubmit()

        // When
        viewModel.onPromoCodeChange("promo")

        // Then
        assertEquals(FormError.PROMO_CODE_CHARACTERS, viewModel.uiState.value.errors.promoCode)
    }

    @Test
    fun `given a date after the clock's today when onSubmit is called then the date shows DATE_IN_FUTURE`() {
        // Given
        val viewModel = FormViewModel(clock).apply { fillValidForm() }
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
        val viewModel = FormViewModel(clock).apply { fillValidForm() }

        // When
        viewModel.onSubmit()

        // Then
        assertEquals(FormUiState(isSubmitted = true), viewModel.uiState.value)
    }

    @Test
    fun `given a submitted form when onSubmittedMessageShown is called then isSubmitted resets`() {
        // Given
        val viewModel = FormViewModel(clock).apply { fillValidForm() }
        viewModel.onSubmit()

        // When
        viewModel.onSubmittedMessageShown()

        // Then
        assertFalse(viewModel.uiState.value.isSubmitted)
    }

    @Test
    fun `given a successful submit when a field changes then no errors are shown`() {
        // Given
        val viewModel = FormViewModel(clock).apply { fillValidForm() }
        viewModel.onSubmit()

        // When
        viewModel.onEmailChange("tiago@")

        // Then
        assertFalse(viewModel.uiState.value.errors.hasErrors)
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
