package pt.tiagoduarte.challenge.listing.presentation.productdetail

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pt.tiagoduarte.challenge.R
import pt.tiagoduarte.challenge.fakes.FakeProductRepository
import pt.tiagoduarte.challenge.random.nextProduct
import pt.tiagoduarte.challenge.ui.theme.AppTheme
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ProductDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `given a saved product when the screen opens then shows its details`() {
        // Given
        val product = Random.nextProduct(title = "Kiwi", stock = 42)

        // When
        showScreen(productId = product.id, repository = FakeProductRepository(listOf(product)))

        // Then
        composeRule.onNodeWithText(product.title).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.product_detail_stock)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(product.stock.toString()).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `given an id with no saved product when the screen opens then shows it was not found`() {
        // Given
        val product = Random.nextProduct()

        // When
        showScreen(productId = product.id + 1, repository = FakeProductRepository(listOf(product)))

        // Then
        composeRule.onNodeWithText(string(R.string.product_detail_not_found)).assertIsDisplayed()
    }

    @Test
    fun `given the detail screen when back is tapped then reports it`() {
        // Given
        var backClicked = false
        val product = Random.nextProduct()
        showScreen(product.id, FakeProductRepository(listOf(product)), onBackClick = { backClicked = true })

        // When
        composeRule.onNodeWithContentDescription(string(R.string.product_detail_back)).performClick()

        // Then
        assertTrue(backClicked)
    }

    private fun showScreen(productId: Int, repository: FakeProductRepository, onBackClick: () -> Unit = {}) {
        val viewModel = ProductDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(ProductDetailViewModel.PRODUCT_ID_ARG to productId)),
            repository = repository,
        )
        composeRule.setContent {
            AppTheme {
                ProductDetailRoute(onBackClick = onBackClick, viewModel = viewModel)
            }
        }
    }

    private fun string(@StringRes id: Int): String = context.getString(id)
}
