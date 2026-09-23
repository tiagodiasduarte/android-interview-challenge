package pt.tiagoduarte.challenge.listing.presentation.products

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import pt.tiagoduarte.challenge.domain.model.Product
import pt.tiagoduarte.challenge.fakes.FakeProductRepository
import pt.tiagoduarte.challenge.random.nextProduct
import pt.tiagoduarte.challenge.rules.MainDispatcherRule
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `given no products loaded yet when the ViewModel is created then the state starts as Loading`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            assertEquals(ProductsUiState.Loading, viewModel.products.value)
        }

    @Test
    fun `given products in the repository when they are observed then the state becomes Loaded with ui models`() =
        runTest(mainDispatcherRule.dispatcher) {
            // Given
            val product = Random.nextProduct(rating = 4.5)

            // When
            val state = loadedState(listOf(product))

            // Then
            assertEquals(
                ProductsUiState.Loaded(
                    listOf(
                        ProductUiModel(
                            id = product.id,
                            title = product.title,
                            rating = product.rating,
                            ratingCategory = RatingCategory.HIGH,
                        ),
                    ),
                ),
                state,
            )
        }

    @Test
    fun `given a rating below 3 when products are observed then the rating category is LOW`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 2.99)

            // When
            val category = ratingCategoryOf(product)

            // Then
            assertEquals(RatingCategory.LOW, category)
        }

    @Test
    fun `given a rating of exactly 3 when products are observed then the rating category is MEDIUM`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 3.0)

            // When
            val category = ratingCategoryOf(product)

            // Then
            assertEquals(RatingCategory.MEDIUM, category)
        }

    @Test
    fun `given a rating of exactly 4 when products are observed then the rating category is MEDIUM`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 4.0)

            // When
            val category = ratingCategoryOf(product)

            // Then
            assertEquals(RatingCategory.MEDIUM, category)
        }

    @Test
    fun `given a rating above 4 when products are observed then the rating category is HIGH`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 4.01)

            // When
            val category = ratingCategoryOf(product)

            // Then
            assertEquals(RatingCategory.HIGH, category)
        }

    @Test
    fun `given the download fails and no saved products when products are observed then the state is Error`() =
        runTest {
            // Given
            val repository = FakeProductRepository(shouldThrow = true)

            // When
            val state = observedState(repository)

            // Then
            assertEquals(ProductsUiState.Error, state)
        }

    @Test
    fun `given the download fails with saved products when products are observed then the state is Loaded`() =
        runTest {
            // Given
            val product = Random.nextProduct()
            val repository = FakeProductRepository(initialProducts = listOf(product), shouldThrow = true)

            // When
            val state = observedState(repository)

            // Then
            assertEquals(listOf(product.id), (state as ProductsUiState.Loaded).products.map { it.id })
        }

    @Test
    fun `given the download succeeds with no products when products are observed then the state is Loaded empty`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            val state = observedState(repository)

            // Then
            assertEquals(ProductsUiState.Loaded(emptyList()), state)
        }

    @Test
    fun `given the ViewModel is created when init runs then it ensures the catalog is downloaded`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            ProductsViewModel(repository)
            advanceUntilIdle()

            // Then
            assertEquals(1, repository.ensureCatalogDownloadedCallCount)
        }

    private fun TestScope.loadedState(products: List<Product>): ProductsUiState =
        observedState(FakeProductRepository(initialProducts = products))

    private fun TestScope.observedState(repository: FakeProductRepository): ProductsUiState {
        val viewModel = ProductsViewModel(repository)
        backgroundScope.launch { viewModel.products.collect {} }
        advanceUntilIdle()
        return viewModel.products.value
    }

    private fun TestScope.ratingCategoryOf(product: Product): RatingCategory =
        (loadedState(listOf(product)) as ProductsUiState.Loaded).products.single().ratingCategory
}
