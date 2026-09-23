package pt.tiagoduarte.challenge.listing.presentation.products

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
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
            viewModel.uiState.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given the download is in progress and no saved products when the state is observed then it stays Loading`() =
        runTest {
            // Given
            val downloadGate = CompletableDeferred<Unit>()
            val repository = FakeProductRepository(downloadGate = downloadGate)

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.uiState.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                advanceUntilIdle()
                expectNoEvents()

                downloadGate.complete(Unit)
                assertEquals(ProductsUiState.Loaded, awaitItem())
            }
        }

    @Test
    fun `given saved products when the state is observed then it becomes Loaded`() =
        runTest {
            // Given
            val repository = FakeProductRepository(initialProducts = listOf(Random.nextProduct()))

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.uiState.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(ProductsUiState.Loaded, awaitItem())
            }
        }

    @Test
    fun `given the download fails and no saved products when the state is observed then it becomes Error`() =
        runTest {
            // Given
            val repository = FakeProductRepository(shouldThrow = true)

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.uiState.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(ProductsUiState.Error, awaitItem())
            }
        }

    @Test
    fun `given the download fails with saved products when the state is observed then it becomes Loaded`() =
        runTest {
            // Given
            val repository = FakeProductRepository(
                initialProducts = listOf(Random.nextProduct()),
                shouldThrow = true
            )

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.uiState.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(ProductsUiState.Loaded, awaitItem())
            }
        }

    @Test
    fun `given the download succeeds with no products when the state is observed then it becomes Loaded`() =
        runTest {
            // Given
            val repository = FakeProductRepository()

            // When
            val viewModel = ProductsViewModel(repository)

            // Then
            viewModel.uiState.test {
                assertEquals(ProductsUiState.Loading, awaitItem())
                assertEquals(ProductsUiState.Loaded, awaitItem())
            }
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

    @Test
    fun `given saved products when the paged products are observed then emits them as ui models`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 4.5)
            val viewModel = ProductsViewModel(FakeProductRepository(listOf(product)))

            // When
            val products = viewModel.awaitProducts()

            // Then
            assertEquals(
                listOf(
                    ProductUiModel(
                        id = product.id,
                        title = product.title,
                        rating = product.rating,
                        ratingCategory = RatingCategory.HIGH,
                    ),
                ),
                products,
            )
        }

    @Test
    fun `given a rating below 3 when the paged products are observed then the rating category is LOW`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 2.99)
            val viewModel = ProductsViewModel(FakeProductRepository(listOf(product)))

            // When
            val products = viewModel.awaitProducts()

            // Then
            val ratingCategory = products.single().ratingCategory
            assertEquals(RatingCategory.LOW, ratingCategory)
        }

    @Test
    fun `given a rating of exactly 3 when the paged products are observed then the rating category is MEDIUM`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 3.0)
            val viewModel = ProductsViewModel(FakeProductRepository(listOf(product)))

            // When
            val products = viewModel.awaitProducts()

            // Then
            val ratingCategory = products.single().ratingCategory
            assertEquals(RatingCategory.MEDIUM, ratingCategory)
        }

    @Test
    fun `given a rating of exactly 4 when the paged products are observed then the rating category is MEDIUM`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 4.0)
            val viewModel = ProductsViewModel(FakeProductRepository(listOf(product)))

            // When
            val products = viewModel.awaitProducts()

            // Then
            val ratingCategory = products.single().ratingCategory
            assertEquals(RatingCategory.MEDIUM, ratingCategory)
        }

    @Test
    fun `given a rating above 4 when the paged products are observed then the rating category is HIGH`() =
        runTest {
            // Given
            val product = Random.nextProduct(rating = 4.01)
            val viewModel = ProductsViewModel(FakeProductRepository(listOf(product)))

            // When
            val products = viewModel.awaitProducts()

            // Then
            val ratingCategory = products.single().ratingCategory
            assertEquals(RatingCategory.HIGH, ratingCategory)
        }

    private suspend fun ProductsViewModel.awaitProducts(): List<ProductUiModel> {
        var pagingData: PagingData<ProductUiModel>? = null
        products.test {
            pagingData = awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
        return flowOf(requireNotNull(pagingData)).asSnapshot()
    }
}
