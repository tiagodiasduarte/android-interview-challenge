package pt.tiagoduarte.challenge.form.presentation

import android.content.Context
import android.icu.text.DateFormat
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

// Pinned to Portuguese (Portugal) so the date labels below don't depend on the machine running the tests
@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "pt-rPT")
class FormScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    // A Wednesday
    private val today = LocalDate.of(2026, 9, 23)
    private val clock = Clock.fixed(today.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC)

    @Before
    fun setUp() {
        val viewModel = FormViewModel(SavedStateHandle(), clock)
        composeRule.setContent {
            AppTheme {
                FormRoute(viewModel = viewModel)
            }
        }
    }

    @Test
    fun `given an empty form when submitting then every field shows it is required`() {
        // Given an empty form

        // When
        submit()

        // Then
        composeRule.onAllNodesWithText(string(R.string.form_error_empty), useUnmergedTree = true)
            .assertCountEquals(FIELD_COUNT)
    }

    @Test
    fun `given an invalid email and promo code when submitting then shows their specific errors`() {
        // Given
        field(R.string.form_email).performTextInput("tiago@")
        field(R.string.form_promo_code).performTextInput("promo")

        // When
        submit()

        // Then
        composeRule.onNodeWithText(string(R.string.form_error_email), useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.form_error_promo_code_characters), useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun `given the date picker when it opens then Mondays and future days cannot be selected`() {
        // Given
        field(R.string.form_delivery_date).performScrollTo().performClick()

        // When
        val monday = day(LocalDate.of(2026, 9, 21))
        val tuesday = day(LocalDate.of(2026, 9, 22))
        val tomorrow = day(today.plusDays(1))

        // Then
        monday.assertIsNotEnabled()
        tomorrow.assertIsNotEnabled()
        tuesday.assertIsEnabled()
    }

    @Test
    fun `given the date picker when a valid day is confirmed then the field shows it`() {
        // Given
        val date = LocalDate.of(2026, 9, 22)

        // When
        selectDeliveryDate(date)

        // Then
        field(R.string.form_delivery_date)
            .assertTextContains(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    }

    @Test
    fun `given the rating dropdown when an option is picked then the field shows it`() {
        // Given
        field(R.string.form_rating).performScrollTo().performClick()

        // When
        composeRule.onNodeWithText(string(R.string.form_rating_very_good)).performClick()

        // Then
        field(R.string.form_rating).assertTextContains(string(R.string.form_rating_very_good))
    }

    @Test
    fun `given a valid form when submitting then shows it was submitted and clears it`() {
        // Given
        field(R.string.form_name).performTextInput("Tiago")
        field(R.string.form_email).performTextInput("tiago@example.com")
        field(R.string.form_number).performTextInput("912345678")
        field(R.string.form_promo_code).performTextInput("PROMO-A")
        selectDeliveryDate(LocalDate.of(2026, 9, 22))
        field(R.string.form_rating).performScrollTo().performClick()
        composeRule.onNodeWithText(string(R.string.form_rating_very_good)).performClick()

        // When
        submit()

        // Then
        composeRule.onNodeWithText(string(R.string.form_submitted)).assertIsDisplayed()
        composeRule.onNodeWithText("Tiago").assertDoesNotExist()
    }

    private fun string(@StringRes id: Int): String = context.getString(id)

    private fun field(@StringRes label: Int): SemanticsNodeInteraction =
        composeRule.onNodeWithText(string(label))

    private fun submit() {
        composeRule.onNodeWithText(string(R.string.form_submit)).performScrollTo().performClick()
    }

    private fun selectDeliveryDate(date: LocalDate) {
        field(R.string.form_delivery_date).performScrollTo().performClick()
        day(date).performClick()
        composeRule.onNodeWithText(string(R.string.form_date_confirm)).performClick()
    }

    // Material 3's date picker labels each day with its full date in this format, e.g. "terça-feira, 22 de
    // setembro de 2026"; the real current day also gets a "today" marker, so match the date as part of the text.
    // If a Material 3 update changes the label, this lookup is what needs updating.
    private fun day(date: LocalDate): SemanticsNodeInteraction {
        val format = DateFormat.getInstanceForSkeleton("yMMMMEEEEd", PORTUGAL).apply {
            timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
        }
        val millis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        return composeRule.onNode(hasText(format.format(Date(millis)), substring = true))
    }

    private companion object {
        const val FIELD_COUNT = 6
        val PORTUGAL: Locale = Locale.forLanguageTag("pt-PT")
    }
}
