package pt.tiagoduarte.challenge.listing.presentation.products

import android.content.Context
import android.os.Looper
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.fakes.FakeProductRepository
import pt.tiagoduarte.challenge.random.nextProduct
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import java.time.Duration
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ProductsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val kiwi = Random.nextProduct(id = 1, title = "Kiwi")
    private val apple = Random.nextProduct(id = 2, title = "Apple")

    @Test
    fun `given saved products when the screen opens then lists them`() {
        // Given
        val repository = FakeProductRepository(listOf(kiwi, apple))

        // When
        showScreen(repository)

        // Then
        composeRule.onNodeWithText("Kiwi").assertIsDisplayed()
        composeRule.onNodeWithText("Apple").assertIsDisplayed()
    }

    @Test
    fun `given a search that matches nothing when typing it then shows no products found`() {
        // Given
        showScreen(FakeProductRepository(listOf(kiwi, apple)))

        // When
        composeRule.onNodeWithText(string(R.string.product_search_placeholder)).performTextInput("banana")

        // Then
        // The search waits for typing to stop, so keep moving the main looper's clock until the results change
        composeRule.waitUntil(TIMEOUT_MILLIS) {
            shadowOf(Looper.getMainLooper()).idleFor(LOOPER_STEP)
            composeRule.onAllNodes(hasText(string(R.string.product_search_no_results)))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Kiwi").assertDoesNotExist()
    }

    @Test
    fun `given the download fails with nothing saved when the screen opens then shows the error`() {
        // Given
        val repository = FakeProductRepository(shouldThrow = true)

        // When
        showScreen(repository)

        // Then
        composeRule.onNodeWithText(string(R.string.product_list_error)).assertIsDisplayed()
    }

    @Test
    fun `given a listed product when it is tapped then reports its id`() {
        // Given
        var clickedId: Int? = null
        showScreen(FakeProductRepository(listOf(kiwi, apple)), onProductClick = { clickedId = it })

        // When
        composeRule.onNodeWithText("Apple").performClick()

        // Then
        assertEquals(apple.id, clickedId)
    }

    private fun showScreen(repository: FakeProductRepository, onProductClick: (Int) -> Unit = {}) {
        val viewModel = ProductsViewModel(SavedStateHandle(), repository)
        composeRule.setContent {
            AppTheme {
                ProductsRoute(onProductClick = onProductClick, viewModel = viewModel)
            }
        }
    }

    private fun string(@StringRes id: Int): String = context.getString(id)

    private companion object {
        const val TIMEOUT_MILLIS = 5_000L
        val LOOPER_STEP: Duration = Duration.ofMillis(100)
    }
}
