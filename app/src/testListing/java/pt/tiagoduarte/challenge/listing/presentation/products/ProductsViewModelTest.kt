package pt.tiagoduarte.challenge.listing.presentation.products

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.fakes.FakeProductRepository
import pt.tiagoduarte.challenge.rules.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val product = Product(
        id = 1,
        title = "Smartphone",
        description = "A phone",
        price = 799.99,
        discountPercentage = 10.0,
        rating = 4.5,
        stock = 12,
        thumbnail = "https://example.com/thumb.jpg",
    )

    @Test
    fun `given no products loaded yet when the ViewModel is created then the state starts as Loading`() =
        runTest(mainDispatcherRule.dispatcher) {
            // Given
            val repository = FakeProductRepository()

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            assertEquals(ProductsUiState.Loading, viewModel.products.value)
        }

    @Test
    fun `given products in the repository when they are observed then the state becomes Loaded`() =
        runTest(mainDispatcherRule.dispatcher) {
            // Given
            val repository = FakeProductRepository(initialProducts = listOf(product))
            val viewModel = ProductsViewModel(repository)

            // When
            backgroundScope.launch { viewModel.products.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(ProductsUiState.Loaded(listOf(product)), viewModel.products.value)
        }

    @Test
    fun `given the ViewModel is created when init runs then it ensures the catalog is downloaded`() =
        runTest(mainDispatcherRule.dispatcher) {
            // Given
            val repository = FakeProductRepository()

            // When
            ProductsViewModel(repository)
            advanceUntilIdle()

            // Then
            assertEquals(1, repository.ensureCatalogDownloadedCallCount)
        }
}
